/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.task;

import org.pieShare.pieTools.piePlate.model.message.LoopHoleAckMessage;
import org.pieShare.pieTools.piePlate.model.message.LoopHolePunchMessage;
import org.pieShare.pieTools.piePlate.service.loophole.LoopHoleService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;

/**
 *
 * @author Richard
 */
public class LoopHolePuncherTask implements IPieEventTask<LoopHolePunchMessage> {

	private LoopHolePunchMessage msg;
	private LoopHoleService loopHoleService;
	private IBeanService beanService;

	@Override
	public void setEvent(LoopHolePunchMessage msg) {
		this.msg = msg;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setLoopHoleService(LoopHoleService loopHoleService) {
		this.loopHoleService = loopHoleService;
	}

	@Override
	public void run() {
		if (!msg.getTo().equals(loopHoleService.getClientID())) {
			return;
		}

		LoopHoleAckMessage ackMsg = beanService.getBean(LoopHoleAckMessage.class);
		ackMsg.setFrom(loopHoleService.getClientID());
		loopHoleService.send(msg, null, port);
	}

}
