/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks;

import java.io.File;
import org.pieShare.pieShareApp.model.message.api.IFileRequestMessage;
import org.pieShare.pieShareApp.model.message.api.IMetaMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.shareService.CouldNotCreateMetaDataException;
import org.pieShare.pieShareApp.service.shareService.IBitTorrentService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieShareApp.service.shareService.NoLocalFileException;
import org.pieShare.pieShareApp.task.AMessageSendingEventTask;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class FileRequestTask extends AMessageSendingEventTask<IFileRequestMessage> {

	private IShareService shareService;
	private IBitTorrentService bitTorrentService;

	public void setBitTorrentService(IBitTorrentService bitTorrentService) {
		this.bitTorrentService = bitTorrentService;
	}

	public void setShareService(IShareService shareService) {
		this.shareService = shareService;
	}

	@Override
	public void run() {

		//todo: send to all or send only to the requesting client?
		//todo: if i am currently receiving the file I need to forwared the MetaMessage of the sharing instance!
		
		try {				
			File localTmpFile = this.shareService.prepareFile((PieFile) this.msg.getPieFolder());
			byte[] meta = this.bitTorrentService.createMetaInformation(localTmpFile);

			IMetaMessage metaMsg = this.messageFactoryService.getFileTransferMetaMessage();
			metaMsg.setMetaInfo(meta);
			metaMsg.setPieFolder(this.msg.getPieFolder());
			
			PieLogger.trace(this.getClass(), "Sending meta for {} with HashCode {} "
					+ "including PieFile with HashCode {}.", 
					metaMsg.getFileMeta().getFile().getName(), 
					metaMsg.getFileMeta().hashCode(),
					metaMsg.getFileMeta().getFile().hashCode());
			
			this.setDefaultAdresse(metaMsg);
			this.clusterManagementService.sendMessage(metaMsg);
		} catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "Could not send MetaMessage!", ex);
		} catch (NoLocalFileException | CouldNotCreateMetaDataException ex) {
			PieLogger.error(this.getClass(), ex.getMessage(), ex);
		}
	}

}
