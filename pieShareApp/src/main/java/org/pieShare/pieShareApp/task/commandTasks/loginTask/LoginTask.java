/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.commandTasks.loginTask;

import com.mchange.io.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.ILoginTask;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.event.ILoginFinishedListener;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.event.LoginFinished;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.event.enumeration.LoginState;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.exceptions.WrongPasswordException;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.encodeService.api.IEncodeService;
import org.pieShare.pieTools.pieUtilities.service.security.pbe.IPasswordEncryptionService;

/**
 *
 * @author Richard
 */
public class LoginTask implements ILoginTask {

	private final String PWD_FILE = "pwd.pie";
	private final byte[] FILE_TEXT;
	private IPieShareAppConfiguration config;
	private IPasswordEncryptionService passwordEncryptionService;
	private IEncodeService encodeService;
	private IBeanService beanService;
	private LoginCommand command;
	private IDatabaseService databaseService;
	private IClusterManagementService clusterManagementService;
	private IEventBase<ILoginFinishedListener, LoginFinished> loginFinishedEventBase;

	public void setLoginFinishedEventBase(IEventBase<ILoginFinishedListener, LoginFinished> loginFinishedEventBase) {
		this.loginFinishedEventBase = loginFinishedEventBase;
	}

	@Override
	public IEventBase<ILoginFinishedListener, LoginFinished> getLoginFinishedEventBase() {
		return this.loginFinishedEventBase;
	}

	public LoginTask() {
		this.FILE_TEXT = "FILE_TEXT".getBytes();
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setPieShareAppConfig(IPieShareAppConfiguration pieShareAppConfiguration) {
		this.config = pieShareAppConfiguration;
	}

	public void setPasswordEncryptionService(IPasswordEncryptionService service) {
		this.passwordEncryptionService = service;
	}

	public void setEncodeService(IEncodeService encodeService) {
		this.encodeService = encodeService;
	}

	@Override
	public void setLoginCommand(LoginCommand command) {
		this.command = command;
	}

	private void login() throws Exception {
		EncryptedPassword pwd1 = this.passwordEncryptionService.encryptPassword(command.getPlainTextPassword());
		command.setPlainTextPassword(null);

		PlainTextPassword passwordForEncoding = new PlainTextPassword();
		passwordForEncoding.password = pwd1.getPassword();

		File pwdFile = new File(String.format("%s/%s", config.getBaseConfigPath(), PWD_FILE));

		if (pwdFile.exists()) {
			try {
				if (Arrays.equals(encodeService.decrypt(passwordForEncoding, FileUtils.getBytes(pwdFile)), FILE_TEXT)) {
					//return pwd1;
				}
				else {
					throw new WrongPasswordException("The given password was wrong.");
				}
			}
			catch (Exception ex) {
				//ToDo: Handle Wrong password
				PieLogger.info(this.getClass(), "Wrong password, not possible to encrypt file");
				throw new WrongPasswordException("The given password was wrong.", ex);
			}
		}
		else {
			createNewPwdFile(passwordForEncoding);
		}

		PieUser user;
		user = this.beanService.getBean(PieShareAppBeanNames.getPieUser());
		user.setPassword(pwd1);

		if (user.getUserName() == null) {
			user.setUserName(command.getUserName());
			databaseService.persistPieUser(user);
		}

		user.setIsLoggedIn(true);

		try {
			IClusterService clusterService = this.clusterManagementService.connect(user.getCloudName());
		}
		catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "Connect failed!", ex);
		}
	}

	private void createNewPwdFile(PlainTextPassword passwordForEncoding) throws Exception {

		File pwdFile = new File(String.format("%s/%s", config.getBaseConfigPath(), PWD_FILE));

		if (pwdFile.exists()) {
			pwdFile.delete();
		}

		FileOutputStream fos;

		byte[] encr = encodeService.encrypt(passwordForEncoding, FILE_TEXT);

		pwdFile.createNewFile();

		fos = new FileOutputStream(pwdFile);
		fos.write(encr);
		fos.flush();
		fos.close();
	}

	@Override
	public void run() {
		try {
			login();
			loginFinishedEventBase.fireEvent(new LoginFinished(config, LoginState.OK));
		}
		catch (WrongPasswordException ex) {
			loginFinishedEventBase.fireEvent(new LoginFinished(config, LoginState.WrongPassword, ex));
		}
		catch (Exception ex) {
			loginFinishedEventBase.fireEvent(new LoginFinished(config, LoginState.CryptoError, ex));
		}
	}
}
