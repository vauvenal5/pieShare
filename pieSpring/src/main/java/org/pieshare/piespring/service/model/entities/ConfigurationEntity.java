/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.piespring.service.model.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.pieShare.pieShareApp.model.entities.api.IBaseEntity;
import org.pieShare.pieShareApp.model.entities.api.IConfigurationEntity;
import org.pieShare.pieShareApp.model.entities.api.IPieUserEntity;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;

/**
 *
 * @author Richard
 */
@Entity
public class ConfigurationEntity implements IConfigurationEntity {

	@Id
	private String user;
	private String workingDir;
	private String tmpDir;
	private String pwdFile;

	@JoinColumn(name = "configuration", unique = true)
	@OneToOne
	private IPieUserEntity pieUserEntity;

        @Override
	public IPieUserEntity getPieUserEntity() {
		return pieUserEntity;
	}

        @Override
	public void setPieUserEntity(IPieUserEntity pieUserEntity) {
		this.pieUserEntity = pieUserEntity;
	}

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
