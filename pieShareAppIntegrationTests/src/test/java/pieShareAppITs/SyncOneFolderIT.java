/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pieShareAppITs;

import java.io.File;
import java.util.UUID;
import javax.inject.Provider;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.folderMessages.FolderCreateMessage;
import org.pieShare.pieShareApp.task.eventTasks.folderTasks.FolderCreateTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.Assert;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.*;
import pieShareAppITs.helper.ITTasksCounter;
import pieShareAppITs.helper.ITUtil;
import pieShareAppITs.helper.runner.FileSyncMain;
import pieShareAppITs.helper.tasks.TestTask;

/**
 * This Integration Test tries Sync PieFolders
 *
 * @author daniela
 */
public class SyncOneFolderIT {

    private AnnotationConfigApplicationContext context;
    private Process process;
    private String cloudName;
    private String password;

    //Constructor

    public SyncOneFolderIT() {
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

    /**
     * Main and Bot are two instances which simulate two devices, they are
     * connected and should sync files and folders. A folder created in the
     * working directory of the Bot should trigger a FolderCreateMessage and
     * Sync with Main Main should create the same folder in the working
     * directory.
     *
     * @throws Exception
     */
    @Test(timeOut = 120000)
    public void Should_SyncOneFolder_When_FolderCreated() throws Exception {
        PieLogger.info(this.getClass(), "IPv4Prop: {}", System.getProperty("java.net.preferIPv4Stack", "false"));
        ITTasksCounter counter = context.getBean(ITTasksCounter.class);

        //MAIN: Login + private folder
        PieUser user = context.getBean("pieUser", PieUser.class);
        PieShareConfiguration config = user.getPieShareConfiguration();

        IPieExecutorTaskFactory executorFactory = context.getBean("pieExecutorTaskFactory", PieExecutorTaskFactory.class);

                //MAIN factories
        //remove origin registration and connect the test task to this message
        executorFactory.removeTaskRegistration(FolderCreateMessage.class);
        executorFactory.registerTaskProvider(FolderCreateMessage.class, new Provider<TestTask>() {
            @Override
            public TestTask get() {
                return context.getBean(TestTask.class);
            }
        });

        //register the create folder task at the test factory
        IPieExecutorTaskFactory testExecutorFacotry = context.getBean("testTaskFactory", PieExecutorTaskFactory.class);
        testExecutorFacotry.registerTaskProvider(FolderCreateMessage.class, new Provider<FolderCreateTask>() {

            @Override
            public FolderCreateTask get() {
                return context.getBean(FolderCreateTask.class);
            }
        });

        this.cloudName = UUID.randomUUID().toString();
	this.password = UUID.randomUUID().toString();
        //BOT start
        System.out.println("Starting bot!");
        this.process = ITUtil.startProcess(FileSyncMain.class,cloudName, password);

        //MAIN login to cloud
        ITUtil.executeLoginToTestCloud(context,cloudName, password);

        ITUtil.waitForProcessToStartup(this.process);
        System.out.println("Bot started!");

        //BOT: Login + private folder
        PieUser botUser = context.getBean("botPieUser", PieUser.class);
        PieShareConfiguration botConfig = botUser.getPieShareConfiguration();

        //Folder to be synced
        File folderMain = new File(config.getWorkingDir(), "testFolder");
        File folderBot = new File(botConfig.getWorkingDir(), "testFolder");
        folderBot.mkdirs();

        System.out.println("Waiting for transfer complete!");
        while (counter.getCount(FolderCreateTask.class) < 1) {
            Thread.sleep(5000);
        }
        System.out.println("Folder transfer completed!");

        if (counter.getCount(FolderCreateTask.class) == 1) {

            assertTrue(folderMain.exists());
            Assert.assertEquals(folderMain.getName(), folderBot.getName());
        } else {
            fail("Too much folder transerfers?! should be 1 but is : " + counter.getCount(FolderCreateTask.class));
        }
    }

}
