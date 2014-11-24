/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.requestService;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.FileRequestMessage;
import org.pieShare.pieShareApp.model.message.FileTransferMetaMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
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

	private IBeanService beanService;
	private IClusterManagementService clusterManagementService;
	private final ConcurrentHashMap<PieFile, Boolean> requestedFiles;
	private IShareService shareService;

	public RequestService() {
		requestedFiles = new ConcurrentHashMap<>();
	}

	public void setShareService(IShareService shareService) {
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
		if(this.requestedFiles.containsKey(pieFile)) {
                    PieLogger.info(this.getClass(), "File allready requested {}", pieFile.getFileName());
			return;
		}
		
		FileRequestMessage msg = beanService.getBean(PieShareAppBeanNames.getFileRequestMessageName());
		PieUser user = this.beanService.getBean(PieShareAppBeanNames.getPieUser());
		msg.setPieFile(pieFile);
		try {
                    PieLogger.info(this.getClass(), "Sending message to cluster {}", user.getCloudName());
			clusterManagementService.sendMessage(msg, user.getCloudName());
			requestedFiles.put(pieFile, false);
		} catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "Error sending RequestMessage.", ex);
		}
	}

	@Override
	public void anncounceRecived(FileTransferMetaMessage message) {
		if (requestedFiles.containsKey(message.getPieFile()) && requestedFiles.get(message.getPieFile()) == false) {

			requestedFiles.replace(message.getPieFile(), true);
			shareService.handleFile(message);
			this.deleteRequestedFile(message.getPieFile());
		}
	}
	
	/**
	 * If this client is in a process of getting a new file and another client requests the
	 * very same file from us we want to keep our seeder open so we notify the shareService about it.
	 * @param pieFile 
	 */
	@Override
	public synchronized void checkForActiveFileHandle(PieFile pieFile) {
		
		if (requestedFiles.containsKey(pieFile) && requestedFiles.get(pieFile).equals(true)) {
			shareService.handleActiveShare(pieFile);
		}
	}

	@Override
	public synchronized boolean deleteRequestedFile(PieFile pieFile) {
		if (requestedFiles.containsKey(pieFile)) {
			requestedFiles.remove(pieFile);
			return true;
		}
		return false;
	}

}
