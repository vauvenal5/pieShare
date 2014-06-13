/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.task;

import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

/**
 *
 * @author Svetoslav
 */
public class FileMetaTask implements IPieEventTask<FileTransferMetaMessage> {

	FileTransferMetaMessage msg;
	IShareService shareService;

	public void setShareService(IShareService shareService) {
		this.shareService = shareService;
	}

	@Override
	public void setMsg(FileTransferMetaMessage msg) {
		this.msg = msg;
	}

	@Override
	public void run() {
		this.shareService.handleFile(msg);
	}

}
