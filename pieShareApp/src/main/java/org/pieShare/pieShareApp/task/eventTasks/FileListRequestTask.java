/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.eventTasks;

import java.io.IOException;
import java.util.List;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.FileListMessage;
import org.pieShare.pieShareApp.model.message.api.IFileListMessage;
import org.pieShare.pieShareApp.model.message.api.IFileListRequestMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.factoryService.IMessageFactoryService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class FileListRequestTask extends PieEventTaskBase<IFileListRequestMessage> {

	private IFileService fileService;
	private IClusterManagementService clusterManagementService;
	private IBeanService beanService;
	private IMessageFactoryService messageFactoryService;

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	public void setMessageFactoryService(IMessageFactoryService messageFactoryService) {
		this.messageFactoryService = messageFactoryService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	@Override
	public void run() {
		List<PieFile> pieFiles;
		try {
			pieFiles = this.fileService.getAllFiles();
		
			//todo: use bean service instead
			IFileListMessage reply = this.messageFactoryService.getFileListMessage();
            reply.setFileList(pieFiles);
			reply.setAddress(this.msg.getAddress());
			
			PieUser user = this.beanService.getBean(PieShareAppBeanNames.getPieUser());
			reply.getAddress().setChannelId(user.getUserName());
			reply.getAddress().setClusterName(user.getCloudName());

			try {
				this.clusterManagementService.sendMessage(reply);
			}
			catch(ClusterManagmentServiceException ex) {
				//todo: error handling
			}
		
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "RequestTask failed!", ex);
		}
	}
	
}
