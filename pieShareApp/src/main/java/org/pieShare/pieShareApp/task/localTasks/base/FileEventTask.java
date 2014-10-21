/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.localTasks.base;

import java.io.IOException;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.FileChangedMessage;
import org.pieShare.pieShareApp.model.message.base.FileMessageBase;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.api.IFileUtilsService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public abstract class FileEventTask implements IPieTask {
	
	protected String filePath;
	protected IBeanService beanService;
	protected IClusterManagementService clusterManagementService;
	protected IFileUtilsService fileUtilsService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	public void setFileUtilsService(IFileUtilsService fileUtilsService) {
		this.fileUtilsService = fileUtilsService;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	protected void doWork(FileMessageBase msg) {
		try {
		msg.setFile(this.fileUtilsService.getPieFile(this.filePath));
		
		//todo: need somewhere a match between working dir and belonging cloud
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		
		this.clusterManagementService.sendMessage(msg, user.getCloudName());
		} catch (IOException ex) {
			PieLogger.info(this.getClass(), "Local file delete messed up!", ex);
		} catch (ClusterManagmentServiceException ex) {
			PieLogger.info(this.getClass(), "Local file delete messed up!", ex);
		}
	}
	
}
