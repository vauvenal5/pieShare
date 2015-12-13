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
import org.pieShare.pieShareApp.model.message.api.IFileTransferCompleteMessage;
import org.pieShare.pieShareApp.model.message.metaMessage.FileTransferCompleteMessage;
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
import pieShareAppITs.helper.ITTasksCounter;
import pieShareAppITs.helper.ITUtil;
import pieShareAppITs.helper.runner.FileSyncMain;
import pieShareAppITs.helper.tasks.TestTask;

/**
 *
 * @author Svetoslav
 */
public class SyncOneBigFileIT {

	private AnnotationConfigApplicationContext context;
	private Process process;
	private String cloudName;

	public SyncOneBigFileIT() {
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
	
	//todo: create maven profile so this test does not run every time
	//@Test(timeOut = 600000)
	public void syncOneBigFileTest() throws Exception {
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
		this.process = ITUtil.startProcess(FileSyncMain.class, cloudName);

		ITUtil.executeLoginToTestCloud(context, cloudName);

		ITUtil.waitForProcessToStartup(this.process);

		File filex = new File(config.getWorkingDir().getParent(), "test.txt");
                
                TestFileUtils.createFile(filex, 1024);
		
		
		//ITFileUtils.createFile(filex, 4294967296L);
		File fileMain = new File(config.getWorkingDir(), "test.txt");
		
		PieUser botUser = context.getBean("botPieUser", PieUser.class);
		PieShareConfiguration botConfig = botUser.getPieShareConfiguration();
		File fileBot = new File(botConfig.getWorkingDir(), "test.txt");
		
		FileUtils.moveFile(filex, fileMain);
		//FileUtils.moveFile(filex, fileBot);
		
		while (counter.getCount(FileTransferCompleteTask.class) < 1) {
			Thread.sleep(5000);
		}

		if (counter.getCount(FileTransferCompleteTask.class) == 1) {
			
			assertTrue(ITUtil.waitForFileToBeFreed(fileMain, 60));
			assertTrue(ITUtil.waitForFileToBeFreed(fileBot, 60));
			
			boolean filesAreEqual = FileUtils.contentEquals(fileMain, fileBot);

			assertTrue(filesAreEqual);
		}
		else {
			fail("To much file transerfers?!");
		}
	}

	/*@Test(timeOut = 120000)
	 public void syncDeleteFile() {
		
	 }*/
}
