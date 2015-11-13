/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.commandTasks.logoutTask;

import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.LogoutCommand;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieShareApp.task.commandTasks.logoutTask.api.ILogoutTask;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 *
 * @author Richard
 */
public class LogoutTask implements ILogoutTask {

	private IClusterManagementService clusterManagementService;
	private LogoutCommand logoutCommand;
	private IBeanService beanService;
	private IUserService userService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}
	
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private void logout() {

		PieUser user = userService.getUser();
		user.setPassword(null);
		user.setIsLoggedIn(false);
		
		try {
			clusterManagementService.disconnect(logoutCommand.getUserName());
		}
		catch (ClusterServiceException ex) {
			//ToDo: Check if we need to handle this.
		}
	}

	@Override
	public void run() {
		logout();
		if(logoutCommand.getCallback() != null) {
			logoutCommand.getCallback().finished();
		}
	}

	@Override
	public void setEvent(LogoutCommand msg) {
		this.logoutCommand = msg;
	}
}
