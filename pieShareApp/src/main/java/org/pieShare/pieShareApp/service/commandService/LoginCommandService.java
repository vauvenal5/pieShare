/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.commandService;

import java.util.Map;
import org.bouncycastle.util.Arrays;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieShareApp.service.database.DatabaseService;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.commandService.api.ICommandService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.pbe.IPasswordEncryptionService;

/**
 *
 * @author Svetoslav
 */
public class LoginCommandService implements ICommandService<LoginCommand> {

	private IPasswordEncryptionService passwordEncryptionService;
	private IBeanService beanService;
	private IClusterManagementService clusterManagementService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	public void setPasswordEncryptionService(IPasswordEncryptionService service) {
		this.passwordEncryptionService = service;
	}

	private IDatabaseService databaseService;

	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	@Override
	public void executeCommand(LoginCommand command) {
		EncryptedPassword pwd = this.passwordEncryptionService.encryptPassword(command.getPlainTextPassword());

		PieUser user = databaseService.getPieUser(command.getUserName());

		/*if (user != null) {
			if (Arrays.areEqual(pwd.getPassword(), user.getPassword().getPassword())) {
				user.setIsLoggedIn(true);
			}
			else {
				//ToDO: Handle wrong password.
				return;
			}
		} else {*/
			user = this.beanService.getBean(PieShareAppBeanNames.getPieUser());
			user.setPassword(pwd);
			user.setUserName(command.getUserName());
			user.setIsLoggedIn(true);
			databaseService.persistPieUser(user);
		//}

		//this.beanService.getBean(PieShareAppBeanNames.getFileServiceName());
		try {
			IClusterService clusterService = this.clusterManagementService.connect(user.getCloudName());
		} catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "Connect failed!", ex);
		}
	}
}
