/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTest;

import commonTestTools.TestFileUtils;
import java.io.File;
import loadTest.loadTestLib.LUtil;
import loadTest.loadTestLib.LoadTestConfigModel;
import loadTest.loadTestLib.message.AllFilesCompleteMessage;
import loadTest.loadTestLib.task.AllFilesCompleteTask;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.LoginTask;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.ILoginFinished;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.exceptions.WrongPasswordException;
import org.pieShare.pieTools.piePlate.service.cluster.ClusterManagementService;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pieShareAppITs.helper.ITTasksCounter;

/**
 *
 * @author richy
 */
public class LoadTestLT {

    private AnnotationConfigApplicationContext context;
    private boolean isMaster = false;
    private ITTasksCounter counter;
    private int nodeCount = 5;
    private int fileCount = 5;
    private long fileSize = 10;

    @BeforeClass
    public static void setUpClass() throws Exception {
        LUtil.setUpEnviroment();
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
        LUtil.performTearDownDelete();
        context = LUtil.getContext();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
        LUtil.performTearDown(context);
    }

    @Test(timeOut = 600000)
    public void loadTest() throws Exception {

        String userName = "testUser";

        PieUser user = context.getBean("pieUser", PieUser.class);
        PieShareConfiguration config = user.getPieShareConfiguration();
        LoginTask task = context.getBean(LoginTask.class);

        LoginCommand command = new LoginCommand();
        PlainTextPassword pwd = new PlainTextPassword();
        pwd.password = "test".getBytes();
        command.setPlainTextPassword(pwd);
        command.setUserName(userName);
        
        LoadTestConfigModel ltModel = LUtil.readJSONConfig();

        IPieExecutorTaskFactory executorFactory = context.getBean("pieExecutorTaskFactory", PieExecutorTaskFactory.class);

        if (isMaster) {
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

        if (isMaster) {
            user.getPieShareConfiguration().getWorkingDir().mkdirs();
            System.out.println("Creating files!");
            for (int i = 0; i < fileCount; i++) {
                String fileName = String.format("testFile_%s", i);
                File file = new File(user.getPieShareConfiguration().getWorkingDir(), fileName);
                TestFileUtils.createFile(file, fileSize);
            }
            System.out.println("Files successfully created!");
        }

        task.run();

        System.out.println("Waiting for completion!");
        if (isMaster) {
            while (counter.getCount(AllFilesCompleteTask.class) < nodeCount) {
                Thread.sleep(10000);
            }
        } else {
            while (user.getPieShareConfiguration().getWorkingDir().listFiles().length < fileCount) {
                Thread.sleep(10000);
            }

            AllFilesCompleteMessage message = context.getBean(AllFilesCompleteMessage.class);
            ClusterManagementService service = context.getBean(ClusterManagementService.class);
            service.sendMessage(message);
        }
        System.out.println("Finished!");
    }
}
