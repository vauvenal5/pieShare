/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.NewFileMessage;
import org.pieShare.pieShareApp.service.fileService.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.api.IFileUtilsService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class LocalFileCreatedTask implements IPieTask {

	private IFileService fileService;
	private String filePath;
	private IFileUtilsService fileUtilsService;
	private IBeanService beanService;
	private IClusterManagementService clusterManagementService;

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	public void setFileUtilsService(IFileUtilsService fileUtilsService) {
		this.fileUtilsService = fileUtilsService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public void run() {
		File file = new File(this.filePath);
		this.fileService.waitUntilCopyFinished(file);
		
		if (file.isDirectory()) {
			return;
		}

		PieFile pieFile = null;
		try {
			pieFile = this.fileUtilsService.getPieFile(file);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Error Creating PieFile.", ex);
			return;
		}

		NewFileMessage msg = beanService.getBean(PieShareAppBeanNames.getNewFileMessageName());
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		msg.setPieFile(pieFile);
		try {
			clusterManagementService.sendMessage(msg, user.getCloudName());
			PieLogger.info(this.getClass(), "Send new file message. Filepath: {}", pieFile.getRelativeFilePath());
		} catch (ClusterManagmentServiceException ex) {
			PieLogger.error(this.getClass(), "FileService error.", ex);
		}
	}
}
