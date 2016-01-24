/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model;

import java.io.File;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class LocalFileEvent {
	private LocalFileEventType type;
	private File file;
	private long timestamp;
        private File oldFile;
        private byte [] MD5;

	public LocalFileEventType getType() {
		return type;
	}

	public void setType(LocalFileEventType type) {
		this.type = type;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

        public File getOldFile() {
            return oldFile;
        }

        public void setOldFile(File oldFile) {
            this.oldFile = oldFile;
        }


        public byte[] getMD5() {
            return MD5;
        }

        public void setMD5(byte[] MD5) {
            this.MD5 = MD5;
        }
        
        
        
        
}
