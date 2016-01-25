/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.pieFilder;

import java.util.Arrays;
import org.pieShare.pieShareApp.model.api.IBaseModel;

/**
 *
 * @author richy
 */
public class PieFile extends PieFilder implements IBaseModel {

	private byte[] md5;
	
	private long size;

	public PieFile() {
		super();
	}

	public byte[] getMd5() {
		return md5;
	}

	public void setMd5(byte[] md5) {
		this.md5 = md5;
	}

	public long getSize() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PieFile)) {
			return false;
		}

		PieFile f = (PieFile) o;

		if (!Arrays.equals(this.md5, f.md5)) {
			return false;
		}

		return super.equals(f);
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = 79 * hash + Arrays.hashCode(this.md5);
		return hash;
	}

}
