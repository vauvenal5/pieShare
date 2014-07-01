/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.requestService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.message.FileRequestMessage;
import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.service.fileService.PieFile;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class RequestService implements IRequestService {

	private final PieLogger logger = new PieLogger(RequestService.class);
	private IBeanService beanService;
	private IClusterManagementService clusterManagementService;
	private ArrayList<PieFile> requestedFiles;
	private IShareService shareService;

	public void setShareService(IShareService shareService)
	{
		this.shareService = shareService;
	}
	
	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	@Override
	public void requestFile(PieFile pieFile) {
		FileRequestMessage msg = beanService.getBean(PieShareAppBeanNames.getFileRequestMessageName());

		msg.setPieFile(pieFile);
		try {
			clusterManagementService.sendMessage(msg);
		} catch (ClusterManagmentServiceException ex) {
			logger.error("Error sending RequestMessage. Message:" + ex.getMessage());
		}
	}

	@Override
	public synchronized void anncounceRecived(FileTransferMetaMessage message) {
		if(!requestedFiles.contains(message.getPieFile()))
		{
			requestedFiles.add(message.getPieFile());
			shareService.handleFile(message);
		}
	}

	@Override
	public ArrayList<PieFile> getRequestedFileList() {
		return requestedFiles;
	}

	@Override
	public boolean deleteRequestedFile(PieFile pieFile) {
		if (requestedFiles.contains(pieFile)) {
			requestedFiles.remove(pieFile);
			return true;
		}
		return false;
	}

}
