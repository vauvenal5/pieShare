/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks;

import org.pieShare.pieShareApp.model.message.api.IFileTransferCompleteMessage;
import org.pieShare.pieShareApp.service.shareService.IBitTorrentService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class FileTransferCompleteTask extends PieEventTaskBase<IFileTransferCompleteMessage> {

	private IBitTorrentService bitTorentService;

	public void setBitTorentService(IBitTorrentService bitTorentService) {
		this.bitTorentService = bitTorentService;
	}

	@Override
	public void run() {
		this.bitTorentService.remoteClientDone(this.msg.getFileMeta());
	}

}
