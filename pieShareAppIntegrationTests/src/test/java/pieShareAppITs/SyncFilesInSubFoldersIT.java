package pieShareAppITs;

import commonTestTools.TestFileUtils;
import java.io.File;
import java.util.UUID;
import javax.inject.Provider;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.metaMessage.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.task.eventTasks.FileTransferCompleteTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pieShareAppITs.helper.ITTasksCounter;
import pieShareAppITs.helper.ITUtil;
import pieShareAppITs.helper.runner.FileSyncMain;
import pieShareAppITs.helper.tasks.TestTask;

public class SyncFilesInSubFoldersIT {
	private AnnotationConfigApplicationContext context;
    private Process process;
	private String cloudName;
	private String password;
	
	private int folderDepth = 2;

    public SyncFilesInSubFoldersIT() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        ITUtil.setUpEnviroment(true);
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
        ITUtil.performTearDownDelete();
        context = ITUtil.getContext();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
        process.destroy();
        ITUtil.performTearDown(context);
    }
	
	private String createFileNameWithFolders(String fileName, int folderDepth) {
		String completeFileName = "";
		for(int i = 1; i <= folderDepth; i++) {
			completeFileName += "FolderLevel" + i + File.separator;
		}
		completeFileName += fileName;
		return completeFileName;
	}
	
	@Test(timeOut = 120000)
	public void syncFilesInSubFoldersTest() throws Exception {
		PieLogger.info(this.getClass(), "IPv4Prop: {}", System.getProperty("java.net.preferIPv4Stack", "false"));
        ITTasksCounter counter = context.getBean(ITTasksCounter.class);
        PieUser user = context.getBean("pieUser", PieUser.class);
        PieShareConfiguration config = user.getPieShareConfiguration();
        IPieExecutorTaskFactory executorFactory = context.getBean("pieExecutorTaskFactory", PieExecutorTaskFactory.class);

        executorFactory.removeTaskRegistration(FileTransferCompleteMessage.class);
        executorFactory.registerTaskProvider(FileTransferCompleteMessage.class, new Provider<TestTask>() {
            @Override
            public TestTask get() {
                return context.getBean(TestTask.class);
            }
        });

        IPieExecutorTaskFactory testExecutorFacotry = context.getBean("testTaskFactory", PieExecutorTaskFactory.class);
        testExecutorFacotry.registerTaskProvider(FileTransferCompleteMessage.class, new Provider<FileTransferCompleteTask>() {

            @Override
            public FileTransferCompleteTask get() {
                return context.getBean(FileTransferCompleteTask.class);
            }
        });

		this.cloudName = UUID.randomUUID().toString();
		this.password = UUID.randomUUID().toString();
        ITUtil.executeLoginToTestCloud(context, cloudName, password);

        System.out.println("Creating files!");
        for (int i = 1; i <= folderDepth; i++) {
            String fileName = createFileNameWithFolders(String.format("testFile_%s", i), i);
            File file = new File(config.getWorkingDir(), fileName);
			System.out.println("Creating file '" + fileName + "'");
            TestFileUtils.createFile(file, 5);
        }
		
		System.out.println("Starting bot!");
        this.process = ITUtil.startProcess(FileSyncMain.class, cloudName, password);
        ITUtil.waitForProcessToStartup(this.process);
        System.out.println("Bot started!");
		
		PieUser botUser = context.getBean("botPieUser", PieUser.class);
        PieShareConfiguration botConfig = botUser.getPieShareConfiguration();
		
		System.out.println("Waiting for transfer complete!");
        while (counter.getCount(FileTransferCompleteTask.class) < folderDepth) {
            Thread.sleep(5000);
        }
        System.out.println("Recived transfer complete!");

        if (counter.getCount(FileTransferCompleteTask.class) == folderDepth) {

            boolean filesAreEqual = true;

            for (int i = 1; filesAreEqual && i <= folderDepth; i++) {
                String fileName = createFileNameWithFolders(String.format("testFile_%s", i), i);
                File file = new File(user.getPieShareConfiguration().getWorkingDir(), fileName);
                File fileBot = new File(botConfig.getWorkingDir(), fileName);
                assertTrue(ITUtil.waitForFileToBeFreed(file, 30));
                assertTrue(ITUtil.waitForFileToBeFreed(fileBot, 30));
                filesAreEqual = FileUtils.contentEquals(file, fileBot);
                assertTrue(ITUtil.waitForFileToBeFreed(file, 30));
                assertTrue(ITUtil.waitForFileToBeFreed(fileBot, 30));
            }

            assertTrue(filesAreEqual);
        } else {
            fail("To0 many file transfers?!");
        }
	}
}
