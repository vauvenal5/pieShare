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
import org.pieShare.pieShareApp.model.message.api.IFileRequestMessage;
import org.pieShare.pieShareApp.model.message.api.IFileTransferMetaMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.factoryService.IMessageFactoryService;
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
	private IMessageFactoryService messageFactoryService;

	public RequestService() {
		requestedFiles = new ConcurrentHashMap<>();
	}

	public void setMessageFactoryService(IMessageFactoryService messageFactoryService) {
		this.messageFactoryService = messageFactoryService;
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
	public synchronized void requestFile(PieFile pieFile) {
		if(this.requestedFiles.containsKey(pieFile)) {
			PieLogger.info(this.getClass(), "File allready requested {}", pieFile.getFileName());
			return;
		}
		
		IFileRequestMessage msg = this.messageFactoryService.getFileRequestMessage();
		PieUser user = this.beanService.getBean(PieShareAppBeanNames.getPieUser());
		msg.getAddress().setClusterName(user.getCloudName());
		msg.getAddress().setChannelId(user.getUserName());
		msg.setPieFile(pieFile);
		try {
            PieLogger.info(this.getClass(), "Sending message to cluster {}", user.getCloudName());
			clusterManagementService.sendMessage(msg);
			requestedFiles.put(pieFile, false);
		} catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "Error sending RequestMessage.", ex);
		}
	}
	
	//todo: is this really needed?
	/*@Override
	public void requestIsBeingHandled(PieFile file) {
		requestedFiles.replace(file, true);
	}*/

	@Override
	public synchronized boolean isRequested(PieFile file) {
		if (requestedFiles.containsKey(file)) {
			return true;
		}
		return false;
	}
	
	/**
	 * If this client is in a process of getting a new file and another client requests the
	 * very same file from us we want to keep our seeder open so we notify the shareService about it.
	 * @param pieFile 
	 */
	/*@Override
	public synchronized void checkForActiveFileHandle(PieFile pieFile) {
		if (requestedFiles.containsKey(pieFile) && requestedFiles.get(pieFile).equals(true)) {
			return true;
		}
	}*/

	@Override
	public synchronized boolean deleteRequestedFile(PieFile pieFile) {
		if (requestedFiles.containsKey(pieFile)) {
			requestedFiles.remove(pieFile);
			return true;
		}
		return false;
	}

}
