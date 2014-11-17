/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.command;

import org.pieShare.pieShareApp.task.commandTasks.logoutTask.api.ILogoutFinished;
import org.pieShare.pieTools.pieUtilities.model.command.ICommand;

/**
 *
 * @author Richard
 */
public class LogoutCommand extends BaseCommand<ILogoutFinished> implements ICommand<ILogoutFinished> {

	private String userName;

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
