/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.model.message.api.IMetaCommitMessage;
import org.pieShare.pieShareApp.service.factoryService.IMessageFactoryService;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieShareApp.service.shareService.AllreadyInitializedException;
import org.pieShare.pieShareApp.service.shareService.IBitTorrentService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class FileMetaTask extends PieEventTaskBase<FileTransferMetaMessage> {

	private IRequestService requestService;
	private IShareService shareService;
	private IBitTorrentService bitTorrentService;
	private IMessageFactoryService messageFactoryService;
	private IClusterManagementService clusterManagementService;

	public void setShareService(IShareService shareService) {
		this.shareService = shareService;
	}

	public void setRequestService(IRequestService requestService) {
		this.requestService = requestService;
	}

	@Override
	public void run() {
		if(!this.requestService.isRequested(msg.getPieFile())) {
			return;
		}
		
		try {
			File destDir = this.shareService.handleFile(msg.getPieFile());
			
			this.bitTorrentService.handleShareTorrent(msg.getPieFile(), msg.getMetaInfo(), destDir);
			
			IMetaCommitMessage metaCommit = this.messageFactoryService.getMetaCommitMessage();
			metaCommit.setMetaInfo(msg.getMetaInfo());
			metaCommit.setPieFile(msg.getPieFile());
			metaCommit.getAddress().setChannelId(msg.getAddress().getChannelId());
			metaCommit.getAddress().setClusterName(msg.getAddress().getClusterName());
			this.clusterManagementService.sendMessage(metaCommit);
		} catch (AllreadyInitializedException ex) {
			PieLogger.info(this.getClass(), "File is allready being handled!", ex);
		} catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "Could not send MetaMessage!", ex);
		}
	}

}
