/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieshare.piespring.service.model.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import org.pieShare.pieShareApp.model.entities.api.IPieFileEntity;

/**
 *
 * @author Svetoslav
 */
@Entity
public class PieFileEntity implements IPieFileEntity{
	private byte[] md5;
	//@Id
	//private String absoluteWorkingPath;
        @Id
	private String relativeFilePath;
	private String fileName;
	private long lastModified;
	private boolean deleted;
	private boolean synched;
	
	public PieFileEntity(){
		this.synched = true;
	}

        @Override
	public boolean isSynched() {
		return synched;
	}

        @Override
	public void setSynched(boolean synched) {
		this.synched = synched;
	}
	
        /*
        @Override
	public String getAbsoluteWorkingPath() {
		return absoluteWorkingPath;
	}

        @Override
	public void setAbsoluteWorkingPath(String absoluteWorkingPath) {
		this.absoluteWorkingPath = absoluteWorkingPath;
	}
	*/
        @Override
	public byte[] getMd5() {
		return md5;
	}

        @Override
	public boolean isDeleted() {
		return deleted;
	}

        @Override
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

        @Override
	public void setMd5(byte[] md5) {
		this.md5 = md5;
	}

        @Override
	public String getRelativeFilePath() {
		return relativeFilePath;
	}

        @Override
	public void setRelativeFilePath(String relativeFilePath) {
		this.relativeFilePath = relativeFilePath;
	}

        @Override
	public String getFileName() {
		return fileName;
	}

        @Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

        @Override
	public long getLastModified() {
		return lastModified;
	}

        @Override
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
	
}
