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
public class SyncOneBigFileIT {

	private AnnotationConfigApplicationContext context;
	private Process process;

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
	
	@Test(timeOut = 600000)
	public void syncOneBigFileTest() throws Exception {
		ITTasksCounter counter = context.getBean(ITTasksCounter.class);
		PieUser user = context.getBean("pieUser", PieUser.class);
		PieShareConfiguration config = user.getPieShareConfiguration();
		IPieExecutorTaskFactory executorFactory = context.getBean("pieExecutorTaskFactory", PieExecutorTaskFactory.class);

		executorFactory.removeTaskRegistration(FileTransferCompleteMessage.class);
		executorFactory.registerTask(FileTransferCompleteMessage.class, TestTask.class);

		IPieExecutorTaskFactory testExecutorFacotry = context.getBean("testTaskFactory", PieExecutorTaskFactory.class);
		testExecutorFacotry.registerTask(FileTransferCompleteMessage.class, FileTransferCompleteTask.class);

		this.process = ITUtil.startProcess(FileSyncMain.class);

		ITUtil.executeLoginToTestCloud(context);

		ITUtil.waitForProcessToStartup(this.process);

		File filex = new File(config.getWorkingDir().getParent(), "test.txt");
		
		ProcessBuilder pb;
		
		if(System.getProperty("os.name").toLowerCase().contains("win")) {
			//fsutil file createnew file.out 1000000000 
			pb = new ProcessBuilder("fsutil", "file", "createnew", filex.getAbsolutePath(), "1073741824");
		}
		else {
			//dd if=/dev/zero of=file.out bs=1MB count=1024 
			pb = new ProcessBuilder("dd", "if=/dev/zero", "of="+filex.getAbsolutePath(), "bs=1MB", "count=1024");
		}
		
		Process p = pb.start();
		p.waitFor();
		
		
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
			
			boolean filesAreEqual = FileUtils.contentEquals(fileMain, fileBot);

			assertTrue(filesAreEqual);
			assertTrue(ITUtil.waitForFileToBeFreed(fileMain, 30));
			assertTrue(ITUtil.waitForFileToBeFreed(fileBot, 30));
		}
		else {
			fail("To much file transerfers?!");
		}
	}

	/*@Test(timeOut = 120000)
	 public void syncDeleteFile() {
		
	 }*/
}
