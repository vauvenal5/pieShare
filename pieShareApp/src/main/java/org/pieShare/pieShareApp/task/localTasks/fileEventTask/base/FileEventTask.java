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
import org.pieShare.pieShareApp.service.fileFilterService.api.IFileFilterService;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileUtilsService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieTask;
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
	private IFileFilterService fileFilterService;

	public void setFileFilterService(IFileFilterService fileFilterService) {
		this.fileFilterService = fileFilterService;
	}

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

	protected boolean checkFilter(PieFile file) {
		return fileFilterService.checkFile(file);
	}

	protected boolean checkFilter(File file) {
		return fileFilterService.checkFile(file);
	}

	protected void doWork(FileMessageBase msg) {
		try {
			this.doWork(msg, this.fileUtilsService.getPieFile(this.filePath));
		} catch (IOException ex) {
			PieLogger.info(this.getClass(), "Local file delete messed up!", ex);
		}
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
