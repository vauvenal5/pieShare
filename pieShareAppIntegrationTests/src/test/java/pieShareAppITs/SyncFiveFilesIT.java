/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import sun.misc.UUDecoder;

/**
 *
 * @author vauvenal5
 */
public class SyncFiveFilesIT {

    private AnnotationConfigApplicationContext context;
    private Process process;
	private String cloudName;
	private String password;

    public SyncFiveFilesIT() {
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

    @Test(timeOut = 240000)
    public void syncFiveFilesTest() throws Exception {
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
        for (int i = 0; i < 5; i++) {
            String fileName = String.format("testFile_%s", i);
            File file = new File(config.getWorkingDir(), fileName);
            TestFileUtils.createFile(file, 5);
        }

        System.out.println("Starting bot!");
        this.process = ITUtil.startProcess(FileSyncMain.class, cloudName, password);
        ITUtil.waitForProcessToStartup(this.process);
        System.out.println("Bot started!");

        /*File filex = new File(config.getWorkingDir().getParent(), "test.txt");
                TestFileUtils.createFile(filex, 2);
		File fileMain = new File(config.getWorkingDir(), "test.txt");*/
        PieUser botUser = context.getBean("botPieUser", PieUser.class);
        PieShareConfiguration botConfig = botUser.getPieShareConfiguration();
        //File fileBot = new File(botConfig.getWorkingDir(), "test.txt");

        //FileUtils.moveFile(filex, fileMain);
        //FileUtils.moveFile(filex, fileBot);
        System.out.println("Waiting for transfer complete!");
        while (counter.getCount(FileTransferCompleteTask.class) < 5) {
            Thread.sleep(5000);
        }
        System.out.println("Recived transfer complete!");

        if (counter.getCount(FileTransferCompleteTask.class) == 5) {

            boolean filesAreEqual = true;

            for (int i = 0; filesAreEqual && i < 5; i++) {
                String fileName = String.format("testFile_%s", i);
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
            fail("To much file transerfers?!");
        }
    }
}
