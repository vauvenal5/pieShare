/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pieShareAppITs;

import pieShareAppITs.helper.runner.FileSyncMain;
import pieShareAppITs.helper.config.PieShareAppServiceConfig;
import pieShareAppITs.helper.ITUtil;
import pieShareAppITs.helper.ITFileUtils;
import pieShareAppITs.helper.ITTasksCounter;
import pieShareAppITs.helper.tasks.TestTask;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.model.message.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.service.PieShareService;
import org.pieShare.pieShareApp.service.commandService.LoginCommandService;
import org.pieShare.pieShareApp.service.configurationService.PieShareAppConfiguration;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.task.eventTasks.FileTransferCompleteTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.testng.Assert.*;
import org.testng.annotations.*;

/**
 *
 * @author Svetoslav
 */
public class SyncOneFileTest {
	
	private AnnotationConfigApplicationContext context;
	private Process process;
	
	public SyncOneFileTest() {
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
		IPieShareAppConfiguration config = context.getBean("pieShareAppMainConfiguration", PieShareAppConfiguration.class);
		IPieExecutorTaskFactory executorFactory = context.getBean("pieExecutorTaskFactory", PieExecutorTaskFactory.class);
		
		executorFactory.removeTaskRegistration(FileTransferCompleteMessage.class);
		executorFactory.registerTask(FileTransferCompleteMessage.class, TestTask.class);
		
		IPieExecutorTaskFactory testExecutorFacotry = context.getBean("testTaskFactory", PieExecutorTaskFactory.class);
		testExecutorFacotry.registerTask(FileTransferCompleteMessage.class, FileTransferCompleteTask.class);
		
		this.process = ITUtil.startProcess(FileSyncMain.class);
		
		ITUtil.executeLoginToTestCloud(context);
		
		File filex = new File(config.getWorkingDirectory().getParent(),"test.txt");
		
		ITFileUtils.createFile(filex, 2048);
		
		File file = new File(config.getWorkingDirectory(),"test.txt");
		
		FileUtils.moveFile(filex, file);
		
		while(counter.getCount(FileTransferCompleteTask.class) < 1) {
			Thread.sleep(5000);
		}
		
		if(counter.getCount(FileTransferCompleteTask.class) == 1) {
			PieShareAppConfiguration botConfig = context.getBean("pieShareAppOtherConfiguration", PieShareAppConfiguration.class);
			
			File file1 = new File(botConfig.getWorkingDirectory(),"test.txt");
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
