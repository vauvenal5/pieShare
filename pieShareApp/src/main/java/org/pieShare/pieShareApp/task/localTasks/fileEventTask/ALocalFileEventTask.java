/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.fileEventTask;

import java.io.File;
import java.io.IOException;
import org.pieShare.pieShareApp.model.message.api.IFilderMessageBase;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.model.pieFile.PieFolder;
import org.pieShare.pieShareApp.service.fileFilterService.api.IFileFilterService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.api.IFileWatcherService;
import org.pieShare.pieShareApp.service.fileService.fileEncryptionService.IFileEncryptionService;
import org.pieShare.pieShareApp.service.historyService.IHistoryService;
import org.pieShare.pieShareApp.task.AMessageSendingTask;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public abstract class ALocalFileEventTask extends AMessageSendingTask {

	protected IFileService fileService;
	protected IHistoryService historyService;
	protected IFileFilterService fileFilterService;
	protected IFileEncryptionService fileEncrypterService;
	protected IFileWatcherService fileWatcherService;
	protected IFileService historyFileService;
	
	protected File file;

	public void setFileWatcherService(IFileWatcherService fileWatcherService) {
		this.fileWatcherService = fileWatcherService;
	}

	public void setFileEncrypterService(IFileEncryptionService fileEncrypterService) {
		this.fileEncrypterService = fileEncrypterService;
	}

	public void setHistoryService(IHistoryService historyService) {
		this.historyService = historyService;
	}
	
	public void setFileFilterService(IFileFilterService fileFilterService) {
		this.fileFilterService = fileFilterService;
	}

	public void setFileService(IFileService fileService) {
		PieLogger.info(this.getClass(), "Setting FileService!");
		this.fileService = fileService;
	}

	public void setHistoryFileService(IFileService historyFileService) {
		this.historyFileService = historyFileService;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	protected PieFile prepareWork() throws IOException {		
		if(!this.fileFilterService.checkFile(this.file)) {
			return null;
		}
		
		this.fileService.waitUntilCopyFinished(this.file);
		
		PieFile pieFile = this.fileService.getPieFile(file);
		
		PieFile oldPieFile = this.historyFileService.getPieFile(this.file);
		
		if(oldPieFile != null && oldPieFile.equals(pieFile)) {
			return null;
		}
		
		if(this.fileWatcherService.isPieFileModifiedByUs(pieFile)) {
			this.fileWatcherService.removePieFileFromModifiedList(pieFile);
			return null;
		}
		
		return pieFile;
	}

	protected<T extends PieFolder> void doWork(IFilderMessageBase<T> msg, T fileOrFolder) {
		try {
			msg.setPieFolder(fileOrFolder);
			
			this.setDefaultAdresse(msg);

			this.clusterManagementService.sendMessage(msg);
		} catch (ClusterManagmentServiceException ex) {
			PieLogger.info(this.getClass(), "Local file or folder delete messed up!", ex);
		}
	}

}
