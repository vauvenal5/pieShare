/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services;

import org.pieshare.piespring.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieShareServer.services.api.IServerService;
import org.pieShare.pieShareServer.services.loopHoleService.LoopHoleService;
import org.pieShare.pieShareServer.services.loopHoleService.api.ILoopHoleService;

/**
 *
 * @author Richard
 */
public class ServerService implements IServerService{

	private IBeanService beanService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}
	
	
	@Override
	public void startServer() {
		PieLogger.info(this.getClass(), "Starting Server!");
		ILoopHoleService loopHoleService = beanService.getBean(LoopHoleService.class);
	}
}
