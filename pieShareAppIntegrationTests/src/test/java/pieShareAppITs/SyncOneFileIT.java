/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pieShareAppITs;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.api.IFileTransferCompleteMessage;
import org.pieShare.pieShareApp.model.message.fileMessageBase.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.task.eventTasks.FileTransferCompleteTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import static org.testng.Assert.fail;
import static org.testng.Assert.fail;
import org.testng.annotations.*;
import pieShareAppITs.helper.ITFileUtils;
import pieShareAppITs.helper.ITTasksCounter;
import pieShareAppITs.helper.ITUtil;
import pieShareAppITs.helper.runner.FileSyncMain;
import pieShareAppITs.helper.tasks.TestTask;

/**
 *
 * @author Svetoslav
 */
public class SyncOneFileIT {

	private AnnotationConfigApplicationContext context;
	private Process process;

	public SyncOneFileIT() {
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

	@Test(timeOut = 300000)
	public void syncOneFileTest() throws Exception {
		ITTasksCounter counter = context.getBean(ITTasksCounter.class);
		PieUser user = context.getBean("pieUser", PieUser.class);
		PieShareConfiguration config = user.getPieShareConfiguration();
		IPieExecutorTaskFactory executorFactory = context.getBean("pieExecutorTaskFactory", PieExecutorTaskFactory.class);

		executorFactory.removeTaskRegistration(FileTransferCompleteMessage.class);
		executorFactory.registerTask(FileTransferCompleteMessage.class, TestTask.class);

		IPieExecutorTaskFactory testExecutorFacotry = context.getBean("testTaskFactory", PieExecutorTaskFactory.class);
		testExecutorFacotry.registerTask(FileTransferCompleteMessage.class, FileTransferCompleteTask.class);

		
		System.out.println("Starting bot!");
		this.process = ITUtil.startProcess(FileSyncMain.class);

		ITUtil.executeLoginToTestCloud(context);

		ITUtil.waitForProcessToStartup(this.process);
		System.out.println("Bot started!");

		File filex = new File(config.getWorkingDir().getParent(), "test.txt");
		ITFileUtils.createFile(filex, 2048);
		File fileMain = new File(config.getWorkingDir(), "test.txt");
		
		PieUser botUser = context.getBean("botPieUser", PieUser.class);
		PieShareConfiguration botConfig = botUser.getPieShareConfiguration();
		File fileBot = new File(botConfig.getWorkingDir(), "test.txt");
		
		//FileUtils.moveFile(filex, fileMain);
		FileUtils.moveFile(filex, fileBot);
		
		System.out.println("Waiting for transfer complete!");
		while (counter.getCount(FileTransferCompleteTask.class) < 1) {
			Thread.sleep(5000);
		}
		System.out.println("Recived transfer complete!");

		if (counter.getCount(FileTransferCompleteTask.class) == 1) {
			
			boolean filesAreEqual = FileUtils.contentEquals(fileMain, fileBot);

			assertTrue(filesAreEqual);
			assertTrue(ITUtil.waitForFileToBeFreed(fileMain, 30));
			assertTrue(ITUtil.waitForFileToBeFreed(fileBot, 30));
		}
		else {
			fail("To much file transerfers?!");
		}
	}
}
