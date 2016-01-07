/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks;

import org.pieShare.pieShareApp.model.message.metaMessage.FileTransferCompleteMessage;
import org.pieShare.pieShareApp.service.shareService.IBitTorrentService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class FileTransferCompleteTask extends PieEventTaskBase<FileTransferCompleteMessage> {

	private IBitTorrentService bitTorentService;

	public void setBitTorentService(IBitTorrentService bitTorentService) {
		this.bitTorentService = bitTorentService;
	}

	@Override
	public void run() {
		PieLogger.trace(this.getClass(), "Starting transfer complete for {}.", msg.getPieFilder().getName());
		//todo: this message does not need to work with a Meta Object... PieFile is enough!!!
		this.bitTorentService.clientDone(this.msg.getFileMeta());
	}

}
