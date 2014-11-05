/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model;

import javax.persistence.Column;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;

/**
 *
 * @author Svetoslav
 */
public class PieUser {

	private EncryptedPassword password;
	private String userName = null;
	private String cloudName = null;
	private boolean isLoggedIn = false;
	private boolean hasPasswordFile = false;

	public boolean hasPasswordFile() {
		return hasPasswordFile;
	}

	public void setHasPasswordFile(boolean hasPasswordFile) {
		this.hasPasswordFile = hasPasswordFile;
	}
	
	public boolean isIsLoggedIn() {
		return isLoggedIn;
	}

	public void setIsLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

	public EncryptedPassword getPassword() {
		return password;
	}

	public void setPassword(EncryptedPassword password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
		this.cloudName = userName;
	}

	public String getCloudName() {
		return cloudName;
	}
}
