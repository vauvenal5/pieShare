/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package integrationTests;

import integrationTests.helper.runner.FileSyncMain;
import integrationTests.helper.config.PieShareAppServiceConfig;
import integrationTests.helper.tasks.FileTranserferCompleteTestTask;
import integrationTests.helper.ITUtil;
import integrationTests.helper.ITFileUtils;
import integrationTests.helper.ITTasksCounter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.model.message.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.service.PieShareService;
import org.pieShare.pieShareApp.service.commandService.LoginCommandService;
import org.pieShare.pieShareApp.service.configurationService.PieShareAppConfiguration;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.task.eventTasks.FileTransferCompleteTask;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.testng.Assert.*;

/**
 *
 * @author Svetoslav
 */
public class SyncOneFileTest {
	
	private AnnotationConfigApplicationContext context;
	private Process process;
	
	public SyncOneFileTest() {
	}

	@org.testng.annotations.BeforeClass
	public static void setUpClass() throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("jgroups.logging.log_factory_class", "org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.JGroupsLoggerFactory");
		PieShareAppServiceConfig.main = true;
	}

	@org.testng.annotations.AfterClass
	public static void tearDownClass() throws Exception {
	}

	@org.testng.annotations.BeforeMethod
	public void setUpMethod() throws Exception {
		context = ITUtil.getContext();
	}

	@org.testng.annotations.AfterMethod
	public void tearDownMethod() throws Exception {
		process.destroy();
		
		//shutdown application
		PieShareService service = context.getBean(PieShareService.class);
		service.stop();
		
		//get dirs to delete
		IPieShareAppConfiguration config = context.getBean("pieShareAppConfiguration", PieShareAppConfiguration.class);
		File mainWorkingDir = config.getWorkingDirectory();
		File mainTmpDir = config.getTempCopyDirectory();
		config = context.getBean("pieShareAppOtherConfiguration", PieShareAppConfiguration.class);
		File botWorkingDir = config.getWorkingDirectory();
		File botTmpDir = config.getTempCopyDirectory();
		
		//stop context
		context.close();
		boolean done = false;
		
		while(!done) {
			try {
				FileUtils.deleteDirectory(mainWorkingDir);
				FileUtils.deleteDirectory(mainTmpDir);
				FileUtils.deleteDirectory(botWorkingDir);
				FileUtils.deleteDirectory(botTmpDir);
				done = true;
			} catch(IOException ex) {
				Thread.sleep(1000);
			}
		}
	}
	
	@org.testng.annotations.Test
	public void syncOneFileTest() throws Exception {
		ITTasksCounter counter = context.getBean(ITTasksCounter.class);
		IPieShareAppConfiguration config = context.getBean("pieShareAppMainConfiguration", PieShareAppConfiguration.class);
		IExecutorService executorService = context.getBean(PieExecutorService.class);
		
		executorService.removeTaskRegistration(FileTransferCompleteMessage.class);
		executorService.registerTask(FileTransferCompleteMessage.class, FileTranserferCompleteTestTask.class);
		
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
			
			boolean done = false;
			
			//todo: this has to move to utils: this is a check if the access to the file has been restored
			//after torrent work
			/*while(!done) {
				try {
					Thread.sleep(1000);
					FileInputStream st = new FileInputStream(file);
					done = true;
					st.close();
				} catch (FileNotFoundException ex) {
					//nothing needed to do here
				} catch (IOException ex) {
					//nothing needed to do here
				} catch (InterruptedException ex) {
					//nothing needed to do here
				}
			}*/
			
		}
		else {
			fail("To much file transerfers?!");
		}
	}
	
	//todo: create test if share service will end lock of file
}
