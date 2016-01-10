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
	private String cloudName;
	private String password;
	
	public SyncChangesIT() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		ITUtil.setUpEnviroment(true);
	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
		ITUtil.performTearDownDelete();
		
		//file = ITFileUtils.createFile(new File(ITUtil.getMainWorkingDir(), "test"), 2048);
                file = new File(ITUtil.getMainWorkingDir(), "test");
                file.getParentFile().mkdirs();
                TestFileUtils.createFile(file, 2);
		fileBot = new File(ITUtil.getBotWorkingDir(), "test");
                fileBot.getParentFile().mkdirs();
		FileUtils.copyFile(file, fileBot, true);
		
		this.cloudName = UUID.randomUUID().toString();
		this.password = UUID.randomUUID().toString();
		this.process = ITUtil.startProcess(FileSyncMain.class, this.cloudName, password);
		
		context = ITUtil.getContext();
		
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

		ITUtil.executeLoginToTestCloud(context, this.cloudName, password);
		
		FileUtils.writeByteArrayToFile(file, "hello world".getBytes(), true);
		
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
