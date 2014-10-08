/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.task;

import org.pieShare.pieShareApp.model.message.FileListMessage;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

/**
 *
 * @author Svetoslav
 */
public class FileListTask implements IPieEventTask<FileListMessage>  {

	private FileListMessage msg;
	private IFileService fileService;
	
	@Override
	public void setMsg(FileListMessage msg) {
		this.msg = msg;
	}

	@Override
	public void run() {
		this.fileService.handlePieFilesList(this.msg.getFileList());
	}
	
}
