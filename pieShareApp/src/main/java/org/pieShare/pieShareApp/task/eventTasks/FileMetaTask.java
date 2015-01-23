/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks;

import java.io.File;
import org.pieShare.pieShareApp.model.message.MetaMessage;
import org.pieShare.pieShareApp.model.message.api.IMetaCommitMessage;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieShareApp.service.shareService.IBitTorrentService;
import org.pieShare.pieShareApp.task.AMessageSendingEventTask;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class FileMetaTask extends AMessageSendingEventTask<MetaMessage> {

	private IRequestService requestService;
	private IBitTorrentService bitTorrentService;
	private IFileService fileService;

	public void setRequestService(IRequestService requestService) {
		this.requestService = requestService;
	}

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	public void setBitTorrentService(IBitTorrentService bitTorrentService) {
		this.bitTorrentService = bitTorrentService;
	}

	@Override
	public void run() {
		if(!this.requestService.isRequested(msg.getPieFile())) {
			return;
		}
		
		try {
			File destDir = this.fileService.getAbsoluteTmpPath(msg.getPieFile()).toFile().getParentFile();
			this.bitTorrentService.handleFile(msg.getFileMeta(), destDir);
			
			IMetaCommitMessage metaCommit = this.messageFactoryService.getMetaCommitMessage();
			metaCommit.setMetaInfo(msg.getMetaInfo());
			metaCommit.setPieFile(msg.getPieFile());
			this.setDefaultAdresse(metaCommit);
			this.clusterManagementService.sendMessage(metaCommit);
		} catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "Could not send MetaMessage!", ex);
		}
	}

}
