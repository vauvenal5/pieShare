/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.message;

import java.util.List;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieTools.piePlate.model.message.HeaderMessage;

/**
 *
 * @author Svetoslav
 */
public class FileListMessage extends HeaderMessage {
	
	private List<PieFile> fileList;
	
	public FileListMessage() {
	}
        
        public void setFileList(List<PieFile> list) {
            this.fileList = list;
        }

	public List<PieFile> getFileList() {
		return fileList;
	}
}
