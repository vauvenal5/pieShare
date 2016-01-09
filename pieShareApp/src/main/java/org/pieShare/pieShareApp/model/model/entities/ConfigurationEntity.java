/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.model.entities;

import org.pieShare.pieShareApp.model.entities.api.IConfigurationEntity;

/**
 *
 * @author Richard
 */
public class ConfigurationEntity implements IConfigurationEntity {

	private String user;
	private String workingDir;
	private String tmpDir;
	private String pwdFile;

        @Override
	public String getWorkingDir() {
		return workingDir;
	}

        @Override
	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}

        @Override
	public String getTmpDir() {
		return tmpDir;
	}

        @Override
	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

        @Override
	public String getPwdFile() {
		return pwdFile;
	}

        @Override
	public void setPwdFile(String pwdFile) {
		this.pwdFile = pwdFile;
	}

        @Override
	public String getUser() {
		return user;
	}

        @Override
	public void setUser(String user) {
		this.user = user;
	}
}
