/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.pieshareserver.services;

import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieshare.pieshareserver.services.api.IServerService;
import org.pieshare.pieshareserver.services.loopHoleService.LoopHoleService;
import org.pieshare.pieshareserver.services.loopHoleService.api.ILoopHoleService;

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
		ILoopHoleService loopHoleService = beanService.getBean(LoopHoleService.class);
	}
}
