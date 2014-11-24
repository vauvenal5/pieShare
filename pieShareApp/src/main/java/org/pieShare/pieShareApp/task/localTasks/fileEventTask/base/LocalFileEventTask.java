/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.fileEventTask.base;

import java.io.File;
import java.io.IOException;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.base.FileMessageBase;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.fileFilterService.api.IFileFilterService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.fileEncryptionService.IFileEncryptionService;
import org.pieShare.pieShareApp.service.historyService.IHistoryService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public abstract class LocalFileEventTask implements IPieTask {

	protected IBeanService beanService;
	protected IClusterManagementService clusterManagementService;
	protected IFileService fileService;
	protected IHistoryService historyService;
	protected IFileFilterService fileFilterService;
	protected IFileEncryptionService fileEncrypterService;
	
	protected File file;

	public void setFileEncrypterService(IFileEncryptionService fileEncrypterService) {
		this.fileEncrypterService = fileEncrypterService;
	}

	public void setHistoryService(IHistoryService historyService) {
		this.historyService = historyService;
	}
	
	public void setFileFilterService(IFileFilterService fileFilterService) {
		this.fileFilterService = fileFilterService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	public void setFileService(IFileService fileService) {
		PieLogger.info(this.getClass(), "Setting FileService!");
		this.fileService = fileService;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	protected PieFile prepareWork() throws IOException {		
		if(!this.fileFilterService.checkFile(this.file)) {
			return null;
		}
		
		this.fileService.waitUntilCopyFinished(this.file);
		
		return this.fileEncrypterService.encryptFile(this.fileService.getPieFile(file));
	}

	protected void doWork(FileMessageBase msg, PieFile file) {
		try {
			msg.setFile(file);
			//todo: need somewhere a match between working dir and belonging cloud
			PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());

			this.clusterManagementService.sendMessage(msg, user.getCloudName());
		} catch (ClusterManagmentServiceException ex) {
			PieLogger.info(this.getClass(), "Local file delete messed up!", ex);
		}
	}

}
