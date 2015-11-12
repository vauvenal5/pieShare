/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.piespring.model.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.pieshare.piespring.model.entities.api.IBaseEntity;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;

/**
 *
 * @author Richard
 */
@Entity
public class ConfigurationEntity implements IBaseEntity {

	@Id
	private String user;
	private String workingDir;
	private String tmpDir;
	private String pwdFile;

	@JoinColumn(name = "configuration", unique = true)
	@OneToOne
	private PieUserEntity pieUserEntity;

	public PieUserEntity getPieUserEntity() {
		return pieUserEntity;
	}

	public void setPieUserEntity(PieUserEntity pieUserEntity) {
		this.pieUserEntity = pieUserEntity;
	}

	public String getWorkingDir() {
		return workingDir;
	}

	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}

	public String getTmpDir() {
		return tmpDir;
	}

	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

	public String getPwdFile() {
		return pwdFile;
	}

	public void setPwdFile(String pwdFile) {
		this.pwdFile = pwdFile;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
