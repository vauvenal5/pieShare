/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pieShareAppITs.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.service.PieShareService;
import org.pieShare.pieShareApp.springConfiguration.PiePlateConfiguration;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppModel;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppTasks;
import org.pieShare.pieShareApp.springConfiguration.PieUtilitiesConfiguration;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.LoginTask;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.ILoginFinished;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.exceptions.WrongPasswordException;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.Assert;
import pieShareAppITs.helper.config.PieShareAppModelITConfig;
import pieShareAppITs.helper.config.PieShareAppServiceConfig;

/**
 *
 * @author Svetoslav
 */
public class ITUtil {
	
	public static String getMainWorkingDir() {
		return "workingDirTestMain";
	}
	
	public static String getMainTmpDir() {
		return "pieTempTestMain";
	}
	
	public static String getBotWorkingDir() {
		return "workingDirTestBot";
	}
	
	public static String getBotTmpDir() {
		return "pieTempTestBot";
	}
	
	public static void setUpEnviroment(boolean main) {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("jgroups.logging.log_factory_class", "org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.JGroupsLoggerFactory");
		PieShareAppModelITConfig.main = main;
	}
	
	public static void performTearDown(AnnotationConfigApplicationContext context) throws Exception {
		//shutdown application
		PieShareService service = context.getBean(PieShareService.class);
		service.stop();
		
		PieUser user = context.getBean(PieUser.class);
		
		//get dirs to delete
		//IPieShareAppConfiguration config = context.getBean("pieShareAppConfiguration", PieShareAppConfiguration.class);
		File mainWorkingDir = user.getPieShareConfiguration().getWorkingDir();//config.getWorkingDirectory();
	//	File mainTmpDir = config.getTempCopyDirectory();
	//	config = context.getBean("pieShareAppOtherConfiguration", PieShareAppConfiguration.class);
	//	File botWorkingDir = config.getWorkingDirectory();
	//	File botTmpDir = config.getTempCopyDirectory();
		
		//stop context
		context.close();
		context = null;
		boolean done = false;
		
		while(!done) {
			try {
				FileUtils.deleteDirectory(mainWorkingDir);
			//	FileUtils.deleteDirectory(mainTmpDir);
			//	FileUtils.deleteDirectory(botWorkingDir);
			//	FileUtils.deleteDirectory(botTmpDir);
				done = true;
			} catch(IOException ex) {
				Thread.sleep(1000);
			}
		}
	}
	
	public static Process startProcess(Class mainClazz) throws IOException {
		String separator = System.getProperty("file.separator");
		String classpath = System.getProperty("java.class.path");
		String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
		ProcessBuilder processBuilder = new ProcessBuilder(path, "-cp", classpath, mainClazz.getName());
		return processBuilder.start();
	}
	
	public static void waitForProcessToStartup(Process process) throws Exception {
		InputStream stream = process.getInputStream();
		InputStreamReader in = new InputStreamReader(stream);
		BufferedReader reader = new BufferedReader(in);
		String line = reader.readLine();
		while(!line.equals("!loggedIn")) {
			line = reader.readLine();
		}
	}
	
	public static boolean waitForFileToBeFreed(File file, int sec) {
		boolean done = false;
		int time = 0;
			
		//todo: this has to move to utils: this is a check if the access to the file has been restored
		//after torrent work
		while(!done || time >= sec) {
			try {
				Thread.sleep(1000);
				FileInputStream st = new FileInputStream(file);
				done = true;
				st.close();
				return true;
			} catch (FileNotFoundException ex) {
				//nothing needed to do here
			} catch (IOException ex) {
				//nothing needed to do here
			} catch (InterruptedException ex) {
				//nothing needed to do here
			}
		}
		
		return false;
	}
	
	public static void executeLoginToTestCloud(AnnotationConfigApplicationContext context) throws Exception {
		LoginCommand command = new LoginCommand();
		PlainTextPassword pwd = new PlainTextPassword();
		pwd.password = "test".getBytes();
		command.setPlainTextPassword(pwd);
		command.setUserName("test");
		
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
		
		LoginTask task = context.getBean(LoginTask.class);
		task.setEvent(command);
		task.run();
	}
	
	public static AnnotationConfigApplicationContext getContext() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(PieUtilitiesConfiguration.class);
		context.register(PiePlateConfiguration.class);
		context.register(PieShareAppModel.class);
		context.register(PieShareAppServiceConfig.class);
		context.register(PieShareAppTasks.class);
		context.refresh();
		return context;
	}
}
