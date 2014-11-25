/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.commandTasks.resetPwd;

import java.io.IOException;
import java.nio.file.Files;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.command.ResetPwdCommand;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.task.commandTasks.resetPwd.api.IResetPwdTask;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class ResetPwdTask implements IResetPwdTask {

	private IDatabaseService databaseService;
	private IBeanService beanService;
	private ResetPwdCommand command;

	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	@Override
	public void setEvent(ResetPwdCommand msg) {
		this.command = msg;
	}

	@Override
	public void run() {
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		user.setHasPasswordFile(false);
		databaseService.mergePieUser(user);

		if (user.getPieShareConfiguration().getPwdFile().exists()) {
			try {
				Files.delete(user.getPieShareConfiguration().getPwdFile().toPath());
			}
			catch (IOException ex) {
				//ToDo: Add error callback.
				PieLogger.error(this.getClass(), "Error deleting password file. Maybe this is ok", ex);
			}
		}
		command.getCallback().pwdResetOK();
	}
}
