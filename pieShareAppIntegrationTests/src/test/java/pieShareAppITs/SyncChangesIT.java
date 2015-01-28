/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pieShareAppITs;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.message.api.IFileTransferCompleteMessage;
import org.pieShare.pieShareApp.model.message.metaMessage.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.task.eventTasks.FileTransferCompleteTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.testng.Assert.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pieShareAppITs.helper.ITFileUtils;
import pieShareAppITs.helper.ITTasksCounter;
import pieShareAppITs.helper.ITUtil;
import pieShareAppITs.helper.runner.FileSyncMain;
import pieShareAppITs.helper.tasks.TestTask;

/**
 *
 * @author Svetoslav
 */
public class SyncChangesIT {
	private AnnotationConfigApplicationContext context;
	private Process process;
	private File file;
	private File fileBot;
	
	public SyncChangesIT() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		ITUtil.setUpEnviroment(true);
	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
		ITUtil.performTearDownDelete();
		
		file = ITFileUtils.createFile(new File(ITUtil.getMainWorkingDir(), "test"), 2048);
		fileBot = new File(ITUtil.getBotWorkingDir(), "test");
		FileUtils.copyFile(file, fileBot, true);
		
		this.process = ITUtil.startProcess(FileSyncMain.class);
		
		context = ITUtil.getContext();
		
		IPieExecutorTaskFactory executorFactory = context.getBean("pieExecutorTaskFactory", PieExecutorTaskFactory.class);
		executorFactory.removeTaskRegistration(FileTransferCompleteMessage.class);
		executorFactory.registerTask(FileTransferCompleteMessage.class, TestTask.class);

		IPieExecutorTaskFactory testExecutorFacotry = context.getBean("testTaskFactory", PieExecutorTaskFactory.class);
		testExecutorFacotry.registerTask(FileTransferCompleteMessage.class, FileTransferCompleteTask.class);
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
		process.destroy();
		ITUtil.performTearDown(context);
	}
	
	@Test(timeOut = 120000)
	public void testOneFileChanged() throws Exception {
		ITUtil.waitForProcessToStartup(this.process);
		
		ITTasksCounter counter = context.getBean(ITTasksCounter.class);

		ITUtil.executeLoginToTestCloud(context);
		
		FileUtils.writeByteArrayToFile(file, "hello world".getBytes(), true);
		//FileUtils.writeByteArrayToFile(fileBot, "hello world".getBytes(), true);
		
		while(counter.getCount(FileTransferCompleteTask.class) <= 0) {
			Thread.sleep(1000);
		}
		
		if(counter.getCount(FileTransferCompleteTask.class) == 1) {
			File botFile = new File(ITUtil.getBotWorkingDir(), "test");
			
			boolean filesAreEqual = FileUtils.contentEquals(this.file, botFile);

			assertTrue(filesAreEqual);
			assertTrue(ITUtil.waitForFileToBeFreed(this.file, 30));
			assertTrue(ITUtil.waitForFileToBeFreed(botFile, 30));
		}
		else {
			fail("To much file transerfers?!");
		}
	}
}
