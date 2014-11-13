/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.commandTasks.loginTask;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.service.configurationService.api.IConfigurationFactory;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.api.ILoginTask;
import org.pieShare.pieShareApp.task.commandTasks.loginTask.exceptions.WrongPasswordException;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.encodeService.api.IEncodeService;
import org.pieShare.pieTools.pieUtilities.service.security.pbe.IPasswordEncryptionService;

/**
 *
 * @author Richard
 */
public class LoginTask implements ILoginTask {

	private final byte[] FILE_TEXT;
	private IPasswordEncryptionService passwordEncryptionService;
	private IEncodeService encodeService;
	private IBeanService beanService;
	private LoginCommand command;
	private IDatabaseService databaseService;
	private IClusterManagementService clusterManagementService;
	private IConfigurationFactory configurationFactory;
	private File pwdFile;
	private IFileService fileService;

	public LoginTask() {
		this.FILE_TEXT = "FILE_TEXT".getBytes();
	}

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	public void setConfigurationFactory(IConfigurationFactory configurationFactory) {
		this.configurationFactory = configurationFactory;
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

	public void setPasswordEncryptionService(IPasswordEncryptionService service) {
		this.passwordEncryptionService = service;
	}

	public void setEncodeService(IEncodeService encodeService) {
		this.encodeService = encodeService;
	}

	@Override
	public void setEvent(LoginCommand command) {
		this.command = command;
	}

	private void login() throws Exception {
		EncryptedPassword pwd1 = this.passwordEncryptionService.encryptPassword(command.getPlainTextPassword());

		PieUser user;
		user = this.beanService.getBean(PieShareAppBeanNames.getPieUser());

		user.setPieShareConfiguration(configurationFactory.checkAndCreateConfig(user.getPieShareConfiguration()));
		pwdFile = user.getPieShareConfiguration().getPwdFile();

		if (pwdFile.exists()) {
			try {
				if (!Arrays.equals(encodeService.decrypt(pwd1, FileUtils.readFileToByteArray(pwdFile)), FILE_TEXT)) {
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
			createNewPwdFile(pwd1);
		}

		user.setPassword(pwd1);
		user.setHasPasswordFile(true);

		if (user.getUserName() == null) {
			user.setUserName(command.getUserName());
			//databaseService.persistPieUser(user);
			databaseService.persist(user);
		}
		user.setIsLoggedIn(true);

		//Check and create folders
		try {
			IClusterService clusterService = this.clusterManagementService.connect(user.getCloudName());
		}
		catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "Connect failed!", ex);
		}
	}

	private void createNewPwdFile(EncryptedPassword passwordForEncoding) throws Exception {

		if (pwdFile.exists()) {
			pwdFile.delete();
		}

		byte[] encr = encodeService.encrypt(passwordForEncoding, FILE_TEXT);

		FileUtils.writeByteArrayToFile(pwdFile, encr, true);
	}

	@Override
	public void run() {
		try {
			login();
			command.getCallback().OK();
		}
		catch (WrongPasswordException ex) {
			command.getCallback().wrongPassword(ex);
		}
		catch (Exception ex) {
			command.getCallback().error(ex);
		}
	}

}
