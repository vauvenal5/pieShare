/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.command;

import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.model.command.ICommand;

/**
 *
 * @author Svetoslav
 */
public class LoginCommand implements ICommand {

	private String userName;
	private PlainTextPassword password;

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPlainTextPassword(PlainTextPassword password) {
		this.password = password;
	}

	public PlainTextPassword getPlainTextPassword() {
		return this.password;
	}
}
