/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTest;

import commonTestTools.TestFileUtils;
import commonTestTools.config.PieShareAppServiceTestConfig;
import java.io.File;
import java.util.Date;
import java.util.List;
import javax.inject.Provider;
import loadTest.loadTestLib.LUtil;
import loadTest.loadTestLib.LoadTestConfigModel;
import loadTest.loadTestLib.config.LoadTestConfig;
import loadTest.loadTestLib.helper.LFileComparer;
import loadTest.loadTestLib.message.AllClientsDoneMessage;
import loadTest.loadTestLib.message.AllFilesCompleteMessage;
import loadTest.loadTestLib.message.ClientIsUpMessage;
import loadTest.loadTestLib.message.MasterIsReadyMessage;
import loadTest.loadTestLib.task.AllClientsDoneTask;
import loadTest.loadTestLib.task.AllFilesCompleteTask;
import loadTest.loadTestLib.task.ClientIsUpTask;
import loadTest.loadTestLib.task.MasterIsReadyTask;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.fileService.LocalFileService;
import org.pieShare.pieTools.pieUtilities.service.networkService.INetworkService;
import org.pieShare.pieTools.pieUtilities.service.networkService.NetworkService;
import org.pieShare.pieShareApp.service.shareService.BitTorrentService;
import org.pieShare.pieShareAppFx.springConfiguration.PiePlateConfiguration;
import org.pieShare.pieShareAppFx.springConfiguration.PieShareApp.PieShareAppModel;
import org.pieShare.pieShareAppFx.springConfiguration.PieShareApp.PieShareAppService;
import org.pieShare.pieShareAppFx.springConfiguration.PieShareApp.PieShareAppTasks;
import org.pieShare.pieShareAppFx.springConfiguration.PieUtilitiesConfiguration;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.LoginTask;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.ILoginFinished;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.exceptions.WrongPasswordException;
import org.pieShare.pieTools.piePlate.service.channel.PlainTextChannel;
import org.pieShare.pieTools.piePlate.service.cluster.ClusterManagementService;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
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
@ContextConfiguration(classes = {PieUtilitiesConfiguration.class, PiePlateConfiguration.class,
	PieShareAppModel.class, PieShareAppServiceTestConfig.class, PieShareAppTasks.class, LoadTestConfig.class})
@TestExecutionListeners({DirtiesContextTestExecutionListener.class})
public class LoadTestIT extends AbstractTestNGSpringContextTests {

	//private AnnotationConfigApplicationContext context;
	private ITTasksCounter counter;
	private LFileComparer comparer;

	private List<PieFile> files;
	private LUtil lUtil = new LUtil();

	@BeforeClass
	public static void setUpClass() throws Exception {
		//LUtil.runInDockerCluster();
		LUtil.setUpEnviroment();

		if (LUtil.IsMaster()) {
			LUtil.setUpResultFile();

			Assert.assertTrue(LUtil.startDockerBuild());
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
		PieLogger.info(this.getClass(), "SetupMethod");
		lUtil.performTearDownDelete();
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
		PieLogger.info(this.getClass(), "TeardownMethod");
		lUtil.performTearDown(this.applicationContext);
	}

	@DataProvider(name = "loadTestDataProvider")
	public static Object[][] loadTestDataProvider() throws Exception {

		//if (LUtil.IsMaster()) {
		List<LoadTestConfigModel> ltModels = LUtil.readJSONConfig();

		Object[][] data = new Object[ltModels.size()][1];

		for (int i = 0; i < ltModels.size(); i++) {
			data[i][0] = ltModels.get(i);
		}

		return data;
		//}

		/*LoadTestConfigModel ltModel = new LoadTestConfigModel();
		 ltModel.setFileSize(0);
		 ltModel.setNodeCount(0);
		 int fc = Integer.parseInt(System.getenv("LTFILES"));
		 ltModel.setFileCount(fc);

		 return new Object[][]{{ltModel}};*/
	}

	//@Test
	public void dockerSetUpTest() throws Exception {

	}

	//@Test
	public void selfTest() throws Exception {
		loadTestDataProvider();
	}

	@DirtiesContext
	@Test(dataProvider = "loadTestDataProvider")
	public void loadTest(LoadTestConfigModel ltModel) throws Exception {
		
		//validate context
		PieShareAppService appService = this.applicationContext.getBean(PieShareAppService.class);
		Assert.assertNotNull(appService);
		
		String userName = "testUser";
		PieUser user = this.applicationContext.getBean("pieUser", PieUser.class);
		ClusterManagementService service = this.applicationContext.getBean(ClusterManagementService.class);
		
		PlainTextChannel loadTestChannel = this.applicationContext.getBean(PlainTextChannel.class);
		loadTestChannel.setChannelId("loadTest");
		service.registerChannel("loadTest", loadTestChannel);

		counter = this.applicationContext.getBean(ITTasksCounter.class);

		if (LUtil.IsMaster()) {
			comparer = this.applicationContext.getBean(LFileComparer.class);

			if (LUtil.UseDocker()) {
				INetworkService networkService = this.applicationContext.getBean(NetworkService.class);
				networkService.setNicDisplayName("docker0");
				//start slave nodes
				for (int i = 1; i < ltModel.getNodeCount(); i++) {
					if (!lUtil.startDockerSlave(ltModel)) {
						i--;
					}
				}
			}
		}

		PieShareConfiguration config = user.getPieShareConfiguration();
		config.setPwdFile(new File("./loadTest/pwdFile"));
		config.setTmpDir(new File("./loadTest/tmpDir"));
		config.setWorkingDir(new File("./loadTest/workingDir"));

		LoginTask task = this.applicationContext.getBean(LoginTask.class);

		LoginCommand command = new LoginCommand();
		PlainTextPassword pwd = new PlainTextPassword();
		pwd.password = "test".getBytes();
		command.setPlainTextPassword(pwd);
		command.setUserName(userName);

		IPieExecutorTaskFactory executorFactory = this.applicationContext.getBean("pieExecutorTaskFactory", PieExecutorTaskFactory.class);

		if (LUtil.IsMaster()) {
			executorFactory.registerTaskProvider(AllFilesCompleteMessage.class, new Provider<AllFilesCompleteTask>() {

				@Override
				public AllFilesCompleteTask get() {
					return new AllFilesCompleteTask();
				}
			});
			executorFactory.registerTaskProvider(ClientIsUpMessage.class, new Provider<ClientIsUpTask>() {

				@Override
				public ClientIsUpTask get() {
					return new ClientIsUpTask();
				}
			});
		}
		else {
			executorFactory.registerTaskProvider(AllClientsDoneMessage.class, new Provider<AllClientsDoneTask>() {

				@Override
				public AllClientsDoneTask get() {
					return new AllClientsDoneTask();
				}
			});
			executorFactory.registerTaskProvider(MasterIsReadyMessage.class, new Provider<MasterIsReadyTask>() {

				@Override
				public MasterIsReadyTask get() {
					return new MasterIsReadyTask();
				}
			});
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
		
		//this block syncs master and client nodes so test starts when all ready
		if (LUtil.IsMaster()) {
			while (counter.getCount(ClientIsUpTask.class) < ltModel.getNodeCount() - 1) {
				Thread.sleep(2000);
				PieLogger.debug(this.getClass(),
						String.format("Waiting for nodes to start up! Missing nodes: %s", ltModel.getNodeCount() - 1 - counter.getCount(ClientIsUpTask.class)));
			}
			
			MasterIsReadyMessage masterReadyMsg = this.applicationContext.getBean(MasterIsReadyMessage.class);
			masterReadyMsg.getAddress().setClusterName("loadTest");
			masterReadyMsg.getAddress().setChannelId("loadTest");
			service.sendMessage(masterReadyMsg);
			
		} else {
			ClientIsUpMessage message = this.applicationContext.getBean(ClientIsUpMessage.class);
			message.getAddress().setClusterName("loadTest");
			message.getAddress().setChannelId("loadTest");
			service.sendMessage(message);
			
			while (counter.getCount(MasterIsReadyMessage.class) < 1) {
				Thread.sleep(2000);
			}
		}

		task.setEvent(command);
		task.run();

		System.out.println("Waiting for completion!");
		if (LUtil.IsMaster()) {
			PieLogger.info(this.getClass(), "Master");

			Date start = new Date();

			while (counter.getCount(AllFilesCompleteTask.class) < (ltModel.getNodeCount() - 1)) {
				Thread.sleep(1000);
			}

			Date stop = new Date();

			long resultTime = stop.getTime() - start.getTime();

			Assert.assertTrue(comparer.getResult());

			LUtil.writeCSVResult(ltModel, resultTime);

			AllClientsDoneMessage doneMsg = this.applicationContext.getBean(AllClientsDoneMessage.class);
			doneMsg.getAddress().setClusterName("loadTest");
			doneMsg.getAddress().setChannelId("loadTest");
			service.sendMessage(doneMsg);

		} else {
			PieLogger.info(this.getClass(), "Slave");
			BitTorrentService torrentService = this.applicationContext.getBean(BitTorrentService.class);

			while (user.getPieShareConfiguration().getWorkingDir().listFiles().length < ltModel.getFileCount()
					|| torrentService.activeTorrents()) {
				Thread.sleep(1000);
			}

			PieLogger.info(this.getClass(), "WorkingDirFileCount: " + String.valueOf(user.getPieShareConfiguration().getWorkingDir().listFiles().length));
			PieLogger.info(this.getClass(), "TestModelCount: " + String.valueOf(ltModel.getFileCount()));

			AllFilesCompleteMessage message = this.applicationContext.getBean(AllFilesCompleteMessage.class);

			LocalFileService fileService = this.applicationContext.getBean(LocalFileService.class);
			message.setFiles(fileService.getAllFiles());

			message.getAddress().setClusterName("loadTest");
			message.getAddress().setChannelId("loadTest");
			service.sendMessage(message);

			PieLogger.info(this.getClass(), "Before waiting for AllClientsDone!");
			while (counter.getCount(AllClientsDoneMessage.class) < 1) {
				Thread.sleep(1000);
			}
			PieLogger.info(this.getClass(), "After waiting for AllClientsDone!");

			//this sleep alows the master to be first up in order to receive all client up notifications
			Thread.sleep(5000);
		}

		Date now = new Date();
		System.out.println(now.toString() + ": Finished!");
	}
}
