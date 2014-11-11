/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import org.pieShare.pieShareApp.model.entities.api.IBaseEntity;

/**
 *
 * @author Svetoslav
 */
@Entity
public class PieFileEntity implements IBaseEntity{
	private byte[] md5;
	@Id
	private String absoluteWorkingPath;
	private String relativeFilePath;
	private String fileName;
	private long lastModified;
	private boolean deleted;

	public String getAbsoluteWorkingPath() {
		return absoluteWorkingPath;
	}

	public void setAbsoluteWorkingPath(String absoluteWorkingPath) {
		this.absoluteWorkingPath = absoluteWorkingPath;
	}
	
	public byte[] getMd5() {
		return md5;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setMd5(byte[] md5) {
		this.md5 = md5;
	}

	public String getRelativeFilePath() {
		return relativeFilePath;
	}

	public void setRelativeFilePath(String relativeFilePath) {
		this.relativeFilePath = relativeFilePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
	
}
