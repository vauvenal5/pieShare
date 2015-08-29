/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.pieFile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;
import org.pieShare.pieShareApp.model.api.IBaseModel;

/**
 *
 * @author richy
 */
public class PieFile implements IBaseModel, Comparable<Object> {

	private byte[] md5;
	private String relativeFilePath;
	private String fileName;
	private long lastModified;
	private boolean deleted;

	public PieFile() {
		this.deleted = false;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public byte[] getMd5() {
		return md5;
	}

	public void setMd5(byte[] md5) {
		this.md5 = md5;
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

	public String getRelativeFilePath() {
		return relativeFilePath;
	}

	public void setRelativeFilePath(String relativeFilePath) {
		this.relativeFilePath = relativeFilePath;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PieFile)) {
			return false;
		}
		
		PieFile f = (PieFile)o;
		
		if(this.lastModified != f.lastModified) {
			return false;
		}
		
		if(!Arrays.equals(this.md5, f.md5)) {
			return false;
		}
		
		return this.equalFilePara(f);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + Arrays.hashCode(this.md5);
		hash = 79 * hash + Objects.hashCode(this.relativeFilePath);
		hash = 79 * hash + Objects.hashCode(this.fileName);
		hash = 79 * hash + (int) (this.lastModified ^ (this.lastModified >>> 32));
		hash = 79 * hash + (this.deleted ? 1 : 0);
		return hash;
	}
	
	protected boolean equalFilePara(PieFile f) {
		if(!this.fileName.equals(f.fileName)) {
			return false;
		}
		
		if(!this.relativeFilePath.equals(f.relativeFilePath)) {
			return false;
		}
		
		if(this.deleted != f.deleted) {
			return false;
		} 
		
		return true;
	}

	@Override
	public int compareTo(Object o) {
		PieFile f = (PieFile)o;
		
		if(this.equals(f)) {
			return 0;
		}
		
		if(this.equalFilePara(f) && (this.lastModified > f.lastModified)) {
			return 1;
		}
		
		return -1;
	}

}
