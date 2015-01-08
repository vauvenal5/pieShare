/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks;

import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class FileMetaTask extends PieEventTaskBase<FileTransferMetaMessage> {

	private IRequestService requestService;
	private IShareService shareService;

	public void setShareService(IShareService shareService) {
		this.shareService = shareService;
	}

	public void setRequestService(IRequestService requestService) {
		this.requestService = requestService;
	}

	@Override
	public void run() {
		if(this.requestService.isRequested(msg.getPieFile())) {
			this.shareService.handleFile(msg.getPieFile(), msg.getMetaInfo());
		}
	}

}
