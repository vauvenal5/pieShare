/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.message;

import org.pieShare.pieShareApp.model.message.api.IFileListMessage;
import java.util.List;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieTools.piePlate.model.message.HeaderMessage;
import org.pieShare.pieTools.piePlate.model.message.api.IEncryptedMessage;

/**
 *
 * @author Svetoslav
 */
public class FileListMessage extends HeaderMessage implements IFileListMessage {
	
	private List<PieFile> fileList;
	
	public FileListMessage() {
	}
        
	@Override
        public void setFileList(List<PieFile> list) {
            this.fileList = list;
        }

	@Override
	public List<PieFile> getFileList() {
		return fileList;
	}
}
