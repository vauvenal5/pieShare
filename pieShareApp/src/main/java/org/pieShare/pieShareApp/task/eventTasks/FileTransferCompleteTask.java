/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks;

import org.pieShare.pieShareApp.model.message.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class FileTransferCompleteTask extends PieEventTaskBase<FileTransferCompleteMessage> {

	private IShareService shareService;

	public void setShareService(IShareService shareService) {
		this.shareService = shareService;
	}

	@Override
	public void run() {
		this.shareService.fileTransferComplete(msg);
	}

}
