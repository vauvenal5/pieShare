/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.requestService;

import java.util.concurrent.ConcurrentHashMap;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.fileMessageBase.FileRequestMessage;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.factoryService.IMessageFactoryService;
import org.pieShare.pieShareApp.service.fileFilterService.api.IFileFilterService;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieShareApp.service.comparerService.api.ICompareService;

/**
 *
 * @author Richard
 */
public class RequestService implements IRequestService {

	private IClusterManagementService clusterManagementService;
	private final ConcurrentHashMap<PieFile, Boolean> requestedFiles;
	private IMessageFactoryService messageFactoryService;
	private ICompareService comparerService;
	private IUserService userService;
	private IFileFilterService filterService;

	public RequestService() {
		requestedFiles = new ConcurrentHashMap<>();
	}

	public void setMessageFactoryService(IMessageFactoryService messageFactoryService) {
		this.messageFactoryService = messageFactoryService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}
	
	public void setFilterService(IFileFilterService filterService){
		this.filterService = filterService;
	}

	@Override
	public synchronized void requestFile(PieFile pieFile) {
		if(!(this.checkFile(pieFile)))
		{
			return;
		}
		
		if (this.isRequested(pieFile)) {
			PieLogger.info(this.getClass(), "File allready requested {}", pieFile.getName());
			return;
		}

		//todo: who is responsible for sending the messages? the service or the task?
		//i belive the task
		FileRequestMessage msg = this.messageFactoryService.getFileRequestMessage();
		PieUser user = userService.getUser();
		msg.getAddress().setClusterName(user.getCloudName());
		msg.getAddress().setChannelId(user.getUserName());
		msg.setPieFilder(pieFile);
		try {
			PieLogger.info(this.getClass(), "Sending message to cluster {}", user.getCloudName());
			clusterManagementService.sendMessage(msg);
			requestedFiles.put(pieFile, false);
		} catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "Error sending RequestMessage.", ex);
		}
	}

	//todo: is this really needed?
	@Override
	public synchronized boolean handleRequest(PieFile file) {
		return this.handleRequest(file, false);
	}

	@Override
	public synchronized boolean handleRequest(PieFile file, boolean force) {
		if(!this.checkFile(file)){
			return false;
		}
		
		if (this.isRequested(file)) {
			if (!requestedFiles.get(file)) {
				requestedFiles.replace(file, true);
				return true;
			}
		} else if (force) {
			requestedFiles.put(file, true);
			return true;
		}

		return false;
	}

	@Override
	public synchronized boolean isRequested(PieFile file) {
		if (requestedFiles.containsKey(file)) {
			return true;
		}
		return false;
	}

	/**
	 * If this client is in a process of getting a new file and another client
	 * requests the very same file from us we want to keep our seeder open so we
	 * notify the shareService about it.
	 *
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
		if (this.isRequested(pieFile)) {
			requestedFiles.remove(pieFile);
			return true;
		}
		return false;
	}
	
	/**
	 * Check if sync is allowed
	 * @param pFile
	 * @return boolean true if sync allowed, else false
	 */
	private boolean checkFile(PieFile pFile){
		if(!(this.filterService.checkFile(pFile))){
			PieLogger.info(this.getClass(), "PieFile not allowed to sync {}", pFile.getName());
			return false;
		}
		
		return true;
	}
}
