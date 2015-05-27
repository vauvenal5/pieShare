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
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.springConfiguration.PiePlateConfiguration;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppTasks;
import org.pieShare.pieShareApp.springConfiguration.PieUtilitiesConfiguration;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.LoginTask;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.ILoginFinished;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.exceptions.WrongPasswordException;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.Assert;
import pieShareAppITs.helper.config.PieShareAppModelITConfig;
import pieShareAppITs.helper.config.PieShareAppServiceConfig;
import pieShareAppITs.helper.config.PieShareUtilitiesITConfig;

/**
 *
 * @author Svetoslav
 */
public class ITUtil {

	public static String getMainWorkingDir() {
		return "it/workingDirTestMain";
	}

	public static String getMainTmpDir() {
		return "it/pieTempTestMain";
	}

	public static String getBotWorkingDir() {
		return "it/workingDirTestBot";
	}

	public static String getBotTmpDir() {
		return "it/pieTempTestBot";
	}
	
	public static String getMainKey() {
		return "it/testMainKey";
	}
	
	public static String getBotKey() {
		return "it/testBotKey";
	}
        
        public static String getMainDbDir() {
            return "it/mainDb";
        }
        
        public static String getBotDbDir() {
            return "it/botDb";
        }

	public static void setUpEnviroment(boolean main) {
		//System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("jgroups.logging.log_factory_class", "org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.JGroupsLoggerFactory");
		PieShareAppServiceConfig.main = main;
		PieLogger.info(ITUtil.class, "MainInstance: {}", main);
	}
	
	public static void performTearDownDelete() throws Exception {
		FileUtils.deleteDirectory(new File(getMainWorkingDir()));
		FileUtils.deleteDirectory(new File(getMainTmpDir()));
		FileUtils.deleteDirectory(new File(getBotWorkingDir()));
		FileUtils.deleteDirectory(new File(getBotTmpDir()));
		FileUtils.deleteDirectory(new File(getMainDbDir()));
		FileUtils.deleteDirectory(new File(getBotDbDir()));
		(new File(getMainKey())).delete();
		(new File(getBotKey())).delete();
	}

	public static void performTearDown(AnnotationConfigApplicationContext context) throws Exception {
		//shutdown application
		PieShareService service = context.getBean(PieShareService.class);
		service.stop();

		//get dirs to delete
		/*PieShareConfiguration config = context.getBean("pieUser", PieUser.class).getPieShareConfiguration();
		File mainWorkingDir = config.getWorkingDir();//config.getWorkingDirectory();
		File mainTmpDir = config.getTmpDir();
		File configMain = config.getPwdFile();
		config = context.getBean("botPieUser", PieUser.class).getPieShareConfiguration();
		File botWorkingDir = config.getWorkingDir();
		File botTmpDir = config.getTmpDir();
		File configBot = config.getPwdFile();*/
		
		//stop context
		context.close();
		context = null;
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
		while (!line.equals("!loggedIn")) {
			line = reader.readLine();
		}
	}

	public static boolean waitForFileToBeFreed(File file, int sec) {
		boolean done = false;
		int time = 0;

		//todo: this has to move to utils: this is a check if the access to the file has been restored
		//after torrent work
		while (!done || time >= sec) {
			try {
				Thread.sleep(1000);
				FileInputStream st = new FileInputStream(file);
				done = true;
				st.close();
				return true;
			}
			catch (FileNotFoundException ex) {
				//nothing needed to do here
			}
			catch (IOException ex) {
				//nothing needed to do here
			}
			catch (InterruptedException ex) {
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
				ex.printStackTrace();
				Assert.fail(ex.getLocalizedMessage());
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
		context.register(PieShareUtilitiesITConfig.class);
		context.register(PiePlateConfiguration.class);
		context.register(PieShareAppModelITConfig.class);
		context.register(PieShareAppServiceConfig.class);
		context.register(PieShareAppTasks.class);
		context.refresh();
		return context;
	}
}
