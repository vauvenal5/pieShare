/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks;

import org.pieShare.pieShareApp.model.message.api.IFileTransferCompleteMessage;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class FileTransferCompleteTask extends PieEventTaskBase<IFileTransferCompleteMessage> {

	private IShareService shareService;

	public void setShareService(IShareService shareService) {
		this.shareService = shareService;
	}

	@Override
	public void run() {
		this.shareService.remoteFileTransferComplete(this.msg.getPieFile());
	}

}
