/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task;

import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.service.factoryService.IMessageFactoryService;
import org.pieShare.pieTools.piePlate.model.message.api.IClusterMessage;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieTask;

/**
 *
 * @author Svetoslav
 */
public abstract class AMessageSendingTask implements IPieTask {
	protected IClusterManagementService clusterManagementService;
	protected IMessageFactoryService messageFactoryService;
	protected IBeanService beanService;

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	public void setMessageFactoryService(IMessageFactoryService messageFactoryService) {
		this.messageFactoryService = messageFactoryService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}
	
	protected void setDefaultAdresse(IClusterMessage msg) {
		//todo: need somewhere a match between working dir and belonging cloud
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		msg.getAddress().setChannelId(user.getUserName());
		msg.getAddress().setClusterName(user.getCloudName());
	}
}
