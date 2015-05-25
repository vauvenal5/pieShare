/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTest;

import commonTestTools.TestFileUtils;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import loadTest.loadTestLib.LUtil;
import loadTest.loadTestLib.LoadTestConfigModel;
import loadTest.loadTestLib.message.AllFilesCompleteMessage;
import loadTest.loadTestLib.task.AllFilesCompleteTask;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.service.networkService.INetworkService;
import org.pieShare.pieShareApp.service.networkService.NetworkService;
import org.pieShare.pieShareApp.service.shareService.BitTorrentService;
import org.pieShare.pieShareApp.service.shareService.IBitTorrentService;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.LoginTask;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.ILoginFinished;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.exceptions.WrongPasswordException;
import org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer.JGroupsPieAddress;
import org.pieShare.pieTools.piePlate.service.cluster.ClusterManagementService;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pieShareAppITs.helper.ITTasksCounter;

/**
 *
 * @author richy
 */
public class LoadTestIT {

    private AnnotationConfigApplicationContext context;
    private ITTasksCounter counter;
    private List<Process> slaves;
	private static List<Long> results;

    @BeforeClass
    public static void setUpClass() throws Exception {
        LUtil.setUpEnviroment();
		
		if(LUtil.IsMaster()) {
			results = new ArrayList<>();
			Process proc = LUtil.startDockerBuild();
            int res = proc.waitFor();
            Assert.assertEquals(res, 0);
		}
    }
	
	public static void tearDownClass() throws Exception {
		if(LUtil.IsMaster()) {
			LUtil.writeJSONResults(results);
		}
	}

    @BeforeMethod
    public void setUpMethod() throws Exception {
        LUtil.performTearDownDelete();
        context = LUtil.getContext();
        this.slaves = new ArrayList<>();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
        LUtil.performTearDown(context);
    }
	
	@DataProvider(name="loadTestDataProvider")
	public static Object[][] loadTestDataProvider() throws Exception {
		
		//System.out.println("IsMaster: "+ String.valueOf(LUtil.IsMaster()) + "\n");
		
		if(LUtil.IsMaster()) {
			List<LoadTestConfigModel> ltModels = LUtil.readJSONConfig();

			//System.out.println("DataSize: "+String.valueOf(ltModels.size()) + "\n");
			Object[][] data = new Object[ltModels.size()][1];
			
			for(int i = 0; i<ltModels.size(); i++) {
				data[i][0] = ltModels.get(i);
			}
			
			//System.out.println("DataSize: "+String.valueOf(data.length) + "\n");
			
			return data;
		}
		
		LoadTestConfigModel ltModel = new LoadTestConfigModel();
		ltModel.setFileSize(0);
		ltModel.setNodeCount(0);
		String fileCount = System.getenv("LTFILES");
		System.out.println("FILECOUNT:"+fileCount);
		int fc = Integer.parseInt(fileCount);
		Assert.assertNotNull(fc);
		Assert.assertNotNull(ltModel);
		ltModel.setFileCount(fc);
		
		return new Object[][]{{ltModel}};
	}
	
	@Test
	public void selfTest() throws Exception {
		loadTestDataProvider();
	}
	
	//todo: solve this another way!!!!
	//todo: search for testng test configs like in C# at work

	@Test(dataProvider = "loadTestDataProvider")
    public void loadTest(LoadTestConfigModel ltModel) throws Exception {
        String userName = "testUser";
		PieUser user = context.getBean("pieUser", PieUser.class);
        
        if(LUtil.IsMaster()) {
            /*Process proc = LUtil.startDockerBuild();
            int res = proc.waitFor();
            Assert.assertEquals(res, 0);*/
            INetworkService networkService = context.getBean(NetworkService.class);
            networkService.setNicDisplayName("docker0");
            
            //start slave nodes
            for(int i=1; i<ltModel.getNodeCount();i++) {
                this.slaves.add(LUtil.startDockerSlave(ltModel));
            }
			
			PieShareConfiguration config = user.getPieShareConfiguration();
			config.setPwdFile(new File("./loadTest/pwdFile"));
			config.setTmpDir(new File("./loadTest/tmpDir"));
			config.setWorkingDir(new File("./loadTest/workingDir"));
        }

        LoginTask task = context.getBean(LoginTask.class);

        LoginCommand command = new LoginCommand();
        PlainTextPassword pwd = new PlainTextPassword();
        pwd.password = "test".getBytes();
        command.setPlainTextPassword(pwd);
        command.setUserName(userName);

        IPieExecutorTaskFactory executorFactory = context.getBean("pieExecutorTaskFactory", PieExecutorTaskFactory.class);

        if (LUtil.IsMaster()) {
            executorFactory.registerTask(AllFilesCompleteMessage.class, AllFilesCompleteTask.class);
            counter = context.getBean(ITTasksCounter.class);
        }

        command.setCallback(new ILoginFinished() {

            @Override
            public void error(Exception ex) {
                Assert.fail(ex.getMessage());
            }

            @Override
            public void wrongPassword(WrongPasswordException ex) {
                Assert.fail(ex.getMessage());
            }

            @Override
            public void OK() {
            }
        });

        task.setEvent(command);

        if (LUtil.IsMaster()) {
            user.getPieShareConfiguration().getWorkingDir().mkdirs();
            System.out.println("Creating files!");
            for (int i = 0; i < ltModel.getFileCount(); i++) {
                String fileName = String.format("testFile_%s", i);
                File file = new File(user.getPieShareConfiguration().getWorkingDir(), fileName);
                TestFileUtils.createFile(file, ltModel.getFileSize());
            }
            System.out.println("Files successfully created!");
            PieLogger.info(this.getClass(), "Files successfully created!");
        }

        task.run();

        System.out.println("Waiting for completion!");
        if (LUtil.IsMaster()) {
            PieLogger.info(this.getClass(), "Master");
			Date start = new Date();
			
            while (counter.getCount(AllFilesCompleteTask.class) < (ltModel.getNodeCount()-1)) {
                Thread.sleep(10000);
            }
			Date stop = new Date();
			
			long resultTime = stop.getTime() - start.getTime();
			results.add(resultTime);
			
        } else {
            PieLogger.info(this.getClass(), "Slave");
			BitTorrentService torrentService = context.getBean(BitTorrentService.class);
			
            while (user.getPieShareConfiguration().getWorkingDir().listFiles().length < ltModel.getFileCount() || 
					torrentService.activeTorrents()) {
                Thread.sleep(5000);
            }
            
            PieLogger.info(this.getClass(), "WorkingDirFileCount: " + String.valueOf(user.getPieShareConfiguration().getWorkingDir().listFiles().length));
            PieLogger.info(this.getClass(), "TestModelCount: " + String.valueOf(ltModel.getFileCount()));

            AllFilesCompleteMessage message = context.getBean(AllFilesCompleteMessage.class);
            ClusterManagementService service = context.getBean(ClusterManagementService.class);
            message.getAddress().setClusterName("testUser");
            message.getAddress().setChannelId("testUser");
            service.sendMessage(message);
        }
        System.out.println("Finished!");
    }
}
