/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.api.IFileRequestMessage;
import org.pieShare.pieShareApp.model.message.api.IMetaMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.comparerService.api.ILocalFileCompareService;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareConfiguration;
import org.pieShare.pieShareApp.service.factoryService.IMessageFactoryService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.fileEncryptionService.IFileEncryptionService;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieShareApp.service.shareService.IBitTorrentService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieShareApp.service.shareService.NoLocalFileException;
import org.pieShare.pieShareApp.task.AMessageSendingEventTask;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

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
		
		try {				
			File localTmpFile = this.shareService.prepareFile(this.msg.getPieFile());
			byte[] meta = this.bitTorrentService.anounceTorrent(localTmpFile);

			IMetaMessage metaMsg = this.messageFactoryService.getFileTransferMetaMessage();
			metaMsg.setMetaInfo(meta);
			metaMsg.setPieFile(this.msg.getPieFile());
			this.setDefaultAdresse(metaMsg);
			this.clusterManagementService.sendMessage(metaMsg);
		} catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "Could not send MetaMessage!", ex);
		} catch (NoLocalFileException ex) {
			PieLogger.info(this.getClass(), ex.getMessage());
		}
	}

}
