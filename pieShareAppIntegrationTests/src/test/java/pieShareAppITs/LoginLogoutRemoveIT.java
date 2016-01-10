/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pieShareAppITs;

import java.io.IOException;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.model.command.LogoutCommand;
import org.pieShare.pieShareApp.model.command.ResetPwdCommand;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.LoginTask;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.ILoginFinished;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.exceptions.WrongPasswordException;
import org.pieShare.pieShareApp.task.commandTasks.logoutTask.LogoutTask;
import org.pieShare.pieShareApp.task.commandTasks.logoutTask.api.ILogoutFinished;
import org.pieShare.pieShareApp.task.commandTasks.resetPwd.ResetPwdTask;
import org.pieShare.pieShareApp.task.commandTasks.resetPwd.api.IResetPwdCalback;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pieShareAppITs.helper.ITUtil;

/**
 *
 * @author Richard
 */
public class LoginLogoutRemoveIT {

	private AnnotationConfigApplicationContext context;

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
		ITUtil.performTearDown(context);
	}

	@Test
	public void loginLogoutReset() throws Exception {

		String userName =  UUID.randomUUID().toString();;

		PieUser user = context.getBean(IUserService.class).getUser();
		PieShareConfiguration config = user.getPieShareConfiguration();
		LoginTask task = context.getBean(LoginTask.class);

		LoginCommand command = new LoginCommand();
		PlainTextPassword pwd = new PlainTextPassword();
		pwd.password =  UUID.randomUUID().toString().getBytes();
		command.setPlainTextPassword(pwd);
		command.setUserName(userName);

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

		task.setEvent(command);
		task.run();

		Assert.assertTrue(config.getPwdFile().exists());
		Assert.assertEquals(user.getCloudName(), userName);
		Assert.assertNotNull(user.getPassword());
		Assert.assertTrue(user.isIsLoggedIn() && user.hasPasswordFile());

		LogoutCommand logoutCommand = new LogoutCommand();
		logoutCommand.setUserName(userName);
		logoutCommand.setCallback(new ILogoutFinished() {

			@Override
			public void finished() {
			}
		});

		LogoutTask logoutTask = context.getBean(LogoutTask.class);
		logoutTask.setEvent(logoutCommand);
		logoutTask.run();

		Assert.assertTrue(config.getPwdFile().exists());
		Assert.assertEquals(user.getCloudName(), userName);
		Assert.assertNull(user.getPassword());
		Assert.assertTrue(user.hasPasswordFile());
		Assert.assertFalse(user.isIsLoggedIn());

		ResetPwdCommand resetPwdCommand = new ResetPwdCommand();
		resetPwdCommand.setCallback(new IResetPwdCalback() {

			@Override
			public void pwdResetOK() {
			}
		});
		
		ResetPwdTask resetPwdTask = context.getBean(ResetPwdTask.class);
		resetPwdTask.setEvent(resetPwdCommand);
		resetPwdTask.run();
		
		Assert.assertFalse(config.getPwdFile().exists());
	}

	@Test
	public void logonLogoutLogin() throws Exception {

		String userName =  UUID.randomUUID().toString();;

		PieUser user = context.getBean(IUserService.class).getUser();
		PieShareConfiguration config = user.getPieShareConfiguration();
		LoginTask task = context.getBean(LoginTask.class);

		LoginCommand command = new LoginCommand();
		PlainTextPassword pwd = new PlainTextPassword();
		pwd.password =  UUID.randomUUID().toString().getBytes();
		command.setPlainTextPassword(pwd);
		command.setUserName(userName);

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

		task.setEvent(command);
		task.run();

		Assert.assertTrue(config.getPwdFile().exists());
		Assert.assertEquals(user.getCloudName(), userName);
		Assert.assertNotNull(user.getPassword());
		Assert.assertTrue(user.isIsLoggedIn() && user.hasPasswordFile());

		LogoutCommand logoutCommand = new LogoutCommand();
		logoutCommand.setUserName(userName);
		logoutCommand.setCallback(new ILogoutFinished() {

			@Override
			public void finished() {
			}
		});

		LogoutTask logoutTask = context.getBean(LogoutTask.class);
		logoutTask.setEvent(logoutCommand);
		logoutTask.run();

		Assert.assertTrue(config.getPwdFile().exists());
		Assert.assertEquals(user.getCloudName(), userName);
		Assert.assertNull(user.getPassword());
		Assert.assertTrue(user.hasPasswordFile());
		Assert.assertFalse(user.isIsLoggedIn());

		ResetPwdCommand resetPwdCommand = new ResetPwdCommand();
		resetPwdCommand.setCallback(new IResetPwdCalback() {

			@Override
			public void pwdResetOK() {
			}
		});

		command = new LoginCommand();
		pwd = new PlainTextPassword();
		pwd.password =  UUID.randomUUID().toString().getBytes();
		command.setPlainTextPassword(pwd);
		command.setUserName(userName);

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

		task.setEvent(command);
		task.run();

		Assert.assertTrue(config.getPwdFile().exists());
		Assert.assertEquals(user.getCloudName(), userName);
		Assert.assertNotNull(user.getPassword());
		Assert.assertTrue(user.isIsLoggedIn() && user.hasPasswordFile());

	}

	@Test
	public void resetPwdFile() {
		String userName =  UUID.randomUUID().toString();;

		PieUser user = context.getBean(IUserService.class).getUser();
		PieShareConfiguration config = user.getPieShareConfiguration();
		user.setHasPasswordFile(true);
		user.setUserName(userName);

		if (config.getPwdFile().exists()) {
			if (!config.getPwdFile().delete()) {
				Assert.fail("Cannot delet existing pwd file");
			}
		}

		try {
			FileUtils.writeByteArrayToFile(config.getPwdFile(), "testData".getBytes());
		}
		catch (IOException ex) {
			Assert.fail(ex.getMessage());
		}
	}
}
