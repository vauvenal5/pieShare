/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.task;

import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleAckMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleConnectionMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHolePunchMessage;
import org.pieShare.pieTools.piePlate.service.loophole.api.ILoopHoleService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class LoopHoleConnectionTask implements IPieEventTask<LoopHoleConnectionMessage> {

	private LoopHoleConnectionMessage msg;
	private ILoopHoleService loopHoleService;
	private boolean isWaitingForAck = false;
	private boolean stop = false;
	private String host;
	private int port;
	private IBeanService beanService;

	@Override
	public void setEvent(LoopHoleConnectionMessage msg) {
		this.msg = msg;
	}

	public IBeanService getBeanService() {
		return beanService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setLoopHoleService(ILoopHoleService longHoleService) {
		this.loopHoleService = longHoleService;
	}

	public void setMsg(LoopHoleConnectionMessage msg) {
		this.msg = msg;
	}

	@Override
	public void run() {
                LoopHoleAckMessage ackMsg = beanService.getBean(LoopHoleAckMessage.class);
                loopHoleService.sendToServer(ackMsg);
		int endpoint = 0;

		while (!stop) {

			if (endpoint == 0) {
				host = msg.getClientPrivateIP();
				port = msg.getClientPrivatePort();
				endpoint++;
			}
			else {
				host = msg.getClientPublicIP();
				port = msg.getClientPublicPort();
				endpoint = 0;
			}

			LoopHolePunchMessage punchMsg = beanService.getBean(LoopHolePunchMessage.class);
			punchMsg.setTo(msg.getFromId());
			punchMsg.setFrom(loopHoleService.getClientID());
			punchMsg.setName(loopHoleService.getName());
			
			loopHoleService.addInWaitFromAckQueu(msg.getFromId(), this);
			loopHoleService.send(punchMsg, host, port);
			
			isWaitingForAck = true;
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException ex) {
				PieLogger.error(this.getClass(), "Error while waiting for ACK", ex);
			}
			isWaitingForAck = false;
			loopHoleService.removeTaskFromAckWaitQueue(msg.getFromId());
		}
	}

	public void ackArrived() {
		if (isWaitingForAck) {
			stop = true;
			loopHoleService.newClientAvailable(host, port);
		}
	}

}
