/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package integrationTests.helper;

import integrationTests.helper.config.PieShareAppServiceConfig;
import java.io.IOException;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.service.commandService.LoginCommandService;
import org.pieShare.pieShareApp.springConfiguration.PiePlateConfiguration;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppModel;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppTasks;
import org.pieShare.pieShareApp.springConfiguration.PieUtilitiesConfiguration;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Svetoslav
 */
public class ITUtil {
	
	public static Process startProcess(Class mainClazz) throws IOException {
		String separator = System.getProperty("file.separator");
		String classpath = System.getProperty("java.class.path");
		String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
		ProcessBuilder processBuilder = new ProcessBuilder(path, "-cp", classpath, mainClazz.getName());
		return processBuilder.start();
	}
	
	public static void executeLoginToTestCloud(AnnotationConfigApplicationContext context) {
		LoginCommandService login = context.getBean(LoginCommandService.class);
		LoginCommand command = new LoginCommand();
		PlainTextPassword pwd = new PlainTextPassword();
		pwd.password = "test".toCharArray();
		command.setPlainTextPassword(pwd);
		command.setUserName("test");
		login.executeCommand(command);
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
