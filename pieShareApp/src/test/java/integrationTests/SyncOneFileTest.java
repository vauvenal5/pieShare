/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package integrationTests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.model.message.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.service.commandService.LoginCommandService;
import org.pieShare.pieShareApp.service.configurationService.PieShareAppConfiguration;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.testng.Assert.*;

/**
 *
 * @author Svetoslav
 */
public class SyncOneFileTest {
	
	private AnnotationConfigApplicationContext context;
	private IntegrationTestUtil itUtil;
	private Process process;
	
	public SyncOneFileTest() {
	}

    // TODO add test methods here.
	// The methods must be annotated with annotation @Test. For example:
	//
	// @Test
	// public void hello() {}

	@org.testng.annotations.BeforeClass
	public static void setUpClass() throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("jgroups.logging.log_factory_class", "org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.JGroupsLoggerFactory");
		PieShareAppServiceConfig.configFile = "pieShareTestMain.properties";
	}

	@org.testng.annotations.AfterClass
	public static void tearDownClass() throws Exception {
	}

	@org.testng.annotations.BeforeMethod
	public void setUpMethod() throws Exception {
		context = IntegrationTestUtil.getContext();
		itUtil = context.getBean(IntegrationTestUtil.class);
	}

	@org.testng.annotations.AfterMethod
	public void tearDownMethod() throws Exception {
		process.destroy();
	}
	
	@org.testng.annotations.Test
	public void syncOneFileTest() throws Exception {
		IPieShareAppConfiguration config = context.getBean(PieShareAppConfiguration.class);
		IExecutorService executorService = context.getBean(PieExecutorService.class);
		
		executorService.removeTaskRegistration(FileTransferCompleteMessage.class);
		executorService.registerTask(FileTransferCompleteMessage.class, FileTranserferCompleteTestTask.class);
		
		String separator = System.getProperty("file.separator");
		String classpath = System.getProperty("java.class.path");
		String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
		ProcessBuilder processBuilder = new ProcessBuilder(path, "-cp", classpath, FileSyncMain.class.getName());
		this.process = processBuilder.start();
		
		//todo: something is wrong with the working dir path :S
		//Thread.sleep(30000);
		
		//todo: add timeout
		//todo: teardown: delete files
		LoginCommandService login = context.getBean(LoginCommandService.class);
		LoginCommand command = new LoginCommand();
		PlainTextPassword pwd = new PlainTextPassword();
		pwd.password = "test".toCharArray();
		command.setPlainTextPassword(pwd);
		command.setUserName("test");
		login.executeCommand(command);
		
		File filex = new File(config.getWorkingDirectory().getParent(),"test.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(filex));
		writer.write("This is a small text file witch will be synced!!!");
		writer.flush();
		writer.close();
		
		File file = new File(config.getWorkingDirectory(),"test.txt");
		
		FileUtils.moveFile(filex, file);
		
		while(this.itUtil.getFileTransferCompletedTask() < 1) {
			Thread.sleep(5000);
		}
		
		if(this.itUtil.getFileTransferCompletedTask() == 1) {
			
			File file1 = new File(config.getWorkingDirectory().getParent(),"workingDirTestBot/test.txt");
			
			boolean filesAreEqual = FileUtils.contentEquals(file, file1);
			assertTrue(filesAreEqual);
		}
		else {
			fail("To much file transerfers?!");
		}
	}
}
