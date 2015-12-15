/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pieShareAppITs;

import java.io.File;
import java.util.UUID;
import javax.inject.Provider;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.folderMessages.FolderDeleteMessage;
import org.pieShare.pieShareApp.task.eventTasks.folderTasks.FolderDeleteTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.Assert;
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

/**
 * This Integration Test tries Sync the delete from a PieFolders
 * 
 * @author daniela
 */
public class DeleteOneFolderIT {

    private AnnotationConfigApplicationContext context;
    private Process process;
    private String cloudName;
    private String password;
    //private List<File> folders;

    public DeleteOneFolderIT() {
        //folders = new ArrayList<>();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        ITUtil.setUpEnviroment(true);
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
        ITUtil.performTearDownDelete();

        /*
         for (int i = 0; i < 10; i++) {
         String folderName = "Folder" + String.valueOf(i);
         File folder = new File(ITUtil.getMainWorkingDir(), f);
         folder.getParentFile().mkdirs();
         TestFileUtils.createFile(folder, 2);
         folders.add(folder);
         FileUtils.copyFile(folder, new File(ITUtil.getBotWorkingDir(), folderName), true);
         }
         */
        context = ITUtil.getContext();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
        process.destroy();
        ITUtil.performTearDown(context);
    }

    /**
     * The deleted Folder from Bot should be deleted at Main Main and Bot are
     * two instances which simulate two devices, they are connected and should
     * sync files and folders. A folder deleted in the working directory of the
     * Bot should trigger a FolderDeleteMessage and Sync with Main. Main should
     * delete the same folder in the working directory.
     *
     * @throws Exception
     */
    @Test(timeOut = 120000)
    public void Should_DeleteOneFolder_When_FolderDeleted() throws Exception {
        PieLogger.info(this.getClass(), "IPv4Prop: {}", System.getProperty("java.net.preferIPv4Stack", "false"));
        ITTasksCounter counter = context.getBean(ITTasksCounter.class);

        //MAIN: Login + private folder
        PieUser user = context.getBean("pieUser", PieUser.class);
        PieShareConfiguration config = user.getPieShareConfiguration();

        IPieExecutorTaskFactory executorFactory = context.getBean("pieExecutorTaskFactory", PieExecutorTaskFactory.class);

        //MAIN factories
        //remove origin registration and connect the test task to this message
        executorFactory.removeTaskRegistration(FolderDeleteMessage.class);
        executorFactory.registerTaskProvider(FolderDeleteMessage.class, new Provider<TestTask>() {
            @Override
            public TestTask get() {
                return context.getBean(TestTask.class);
            }
        });

        //register the create folder task at the test factory
        IPieExecutorTaskFactory testExecutorFacotry = context.getBean("testTaskFactory", PieExecutorTaskFactory.class);
        testExecutorFacotry.registerTaskProvider(FolderDeleteMessage.class, new Provider<FolderDeleteTask>() {

            @Override
            public FolderDeleteTask get() {
                return context.getBean(FolderDeleteTask.class);
            }
        });

        this.cloudName = UUID.randomUUID().toString();
        this.password = UUID.randomUUID().toString();

        //BOT: config + private folder
        PieUser botUser = context.getBean("botPieUser", PieUser.class);
        PieShareConfiguration botConfig = botUser.getPieShareConfiguration();

        //Folder Bot to be deleted at Main
        String folderName = "testFolder";
        File folderMain = new File(config.getWorkingDir(), folderName);
        File folderBot = new File(botConfig.getWorkingDir(), folderName);
        folderBot.mkdirs();
        folderMain.mkdirs();

        //BOT start + Login
        System.out.println("Starting bot!");
        this.process = ITUtil.startProcess(FileSyncMain.class, cloudName, password);

        //MAIN login to cloud
        ITUtil.executeLoginToTestCloud(context, cloudName, password);

        ITUtil.waitForProcessToStartup(this.process);
        System.out.println("Bot started!");

        //Assert that folder @bot is really deleted
        Assert.assertTrue(folderBot.delete());

        System.out.println("Waiting for delete to sync!");
        while (counter.getCount(FolderDeleteTask.class) < 1) {
            Thread.sleep(1000);
        }
        System.out.println("Folder delete completed!");

        if (counter.getCount(FolderDeleteTask.class) == 1) {
            //Folder shouldn't exist anymore
            assertTrue(!folderMain.exists());
        } else {
            fail("Too much folder or no deletes?! should be 1 but is : " + counter.getCount(FolderDeleteTask.class));
        }

    }
}
