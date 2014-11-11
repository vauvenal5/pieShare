/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.pieFile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.pieShare.pieShareApp.model.api.IBaseModel;

/**
 *
 * @author richy
 */
public class PieFile implements IBaseModel {

	private byte[] md5;
	private String relativeFilePath;
	private String fileName;
	private long lastModified;

	public PieFile() {

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
	public boolean equals(Object obj) {
		if (!(obj instanceof PieFile)) {
			return false;
		}

		return Arrays.equals(((PieFile) obj).getMd5(), this.md5);
	}

	@Override
	public int hashCode() {
		//TODO: Test this
		ByteBuffer bb = ByteBuffer.wrap(md5);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}

}
