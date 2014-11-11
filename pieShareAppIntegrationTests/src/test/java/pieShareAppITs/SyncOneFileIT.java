/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pieShareAppITs;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieUser;
import pieShareAppITs.helper.runner.FileSyncMain;
import pieShareAppITs.helper.ITUtil;
import pieShareAppITs.helper.ITTasksCounter;
import pieShareAppITs.helper.tasks.TestTask;
import org.pieShare.pieShareApp.model.message.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.service.configurationService.PieShareConfiguration;
import org.pieShare.pieShareApp.task.eventTasks.FileTransferCompleteTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.*;
import pieShareAppITs.helper.ITFileUtils;

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
		context = ITUtil.getContext();
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
		process.destroy();
		ITUtil.performTearDown(context);
	}

	@Test(timeOut = 120000)
	public void syncOneFileTest() throws Exception {
		ITTasksCounter counter = context.getBean(ITTasksCounter.class);
		PieUser user = context.getBean(PieUser.class);
		PieShareConfiguration config = user.getPieShareConfiguration();
		IPieExecutorTaskFactory executorFactory = context.getBean("pieExecutorTaskFactory", PieExecutorTaskFactory.class);

		executorFactory.removeTaskRegistration(FileTransferCompleteMessage.class);
		executorFactory.registerTask(FileTransferCompleteMessage.class, TestTask.class);

		IPieExecutorTaskFactory testExecutorFacotry = context.getBean("testTaskFactory", PieExecutorTaskFactory.class);
		testExecutorFacotry.registerTask(FileTransferCompleteMessage.class, FileTransferCompleteTask.class);

		this.process = ITUtil.startProcess(FileSyncMain.class);
		ITUtil.waitForProcessToStartup(this.process);

		ITUtil.executeLoginToTestCloud(context);

		File filex = new File(config.getWorkingDir().getParent(),"test.txt");
		ITFileUtils.createFile(filex, 2048);
		File file = new File(config.getWorkingDir(),"test.txt");
		FileUtils.moveFile(filex, file);
		while (counter.getCount(FileTransferCompleteTask.class) < 1) {
			Thread.sleep(5000);
		}

		if(counter.getCount(FileTransferCompleteTask.class) == 1) {
			PieUser botUser = context.getBean("botPieUser", PieUser.class);
			PieShareConfiguration botConfig = botUser.getPieShareConfiguration();
			File file1 = new File(botConfig.getWorkingDir(),"test.txt");
			boolean filesAreEqual = FileUtils.contentEquals(file, file1);
			
			assertTrue(filesAreEqual);
			assertTrue(ITUtil.waitForFileToBeFreed(file, 30));
			assertTrue(ITUtil.waitForFileToBeFreed(file1, 30));
		}
		else {
			fail("To much file transerfers?!");
		}
	}

	/*@Test(timeOut = 120000)
	 public void syncDeleteFile() {
		
	 }*/
}
