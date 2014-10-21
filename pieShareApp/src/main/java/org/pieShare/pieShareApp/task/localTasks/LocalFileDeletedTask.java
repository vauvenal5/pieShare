/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.localTasks;

import java.io.File;
import java.io.IOException;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.FileDeletedMessage;
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
public class LocalFileDeletedTask implements IPieTask {
	
	private String filePath;
	private IBeanService beanService;
	private IClusterManagementService clusterManagementService;
	private IFileUtilsService fileUtilsService;

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

	@Override
	public void run() {
		try {
		File file = new File(this.filePath);
		//todo: for the time being we will just delete without checks
		//later somekinde of persistency and check has to be added
		FileDeletedMessage msg = beanService.getBean(PieShareAppBeanNames.getFileDeletedMessage());
		
		msg.setFile(this.fileUtilsService.getPieFile(file));
		
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
