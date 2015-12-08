/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.pieFilder;

import java.util.Arrays;
import java.util.Objects;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;

/**
 *
 * @author Svetoslav
 */
public class FileMeta {
	private PieFile file;
	private byte[] data;

	public PieFile getFile() {
		return file;
	}

	public void setFile(PieFile file) {
		this.file = file;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof FileMeta)) {
			return false;
		}
		
		FileMeta f = (FileMeta)o;
		
		if(!this.file.equals(f.file)) {
			return false;
		}
		
		return Arrays.equals(this.data, f.data);
	}

	@Override
	public int hashCode() {
		int hash = this.file.hashCode();
		hash = 59 * hash + Arrays.hashCode(this.data);
		return hash;
	}
}
