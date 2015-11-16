/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.task;

import javax.inject.Provider;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleAckMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHolePunchMessage;
import org.pieShare.pieTools.piePlate.service.loophole.LoopHoleFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;

/**
 *
 * @author Richard
 */
public class LoopHolePuncherTask implements IPieEventTask<LoopHolePunchMessage> {

    private LoopHolePunchMessage msg;
    private LoopHoleFactory factory;
	private Provider<LoopHoleAckMessage> loopHoleAckMessageProvider;
	
    @Override
    public void setEvent(LoopHolePunchMessage msg) {
        this.msg = msg;
    }

	public void setLoopHoleAckMessageProvider(Provider<LoopHoleAckMessage> loopHoleAckMessageProvider) {
		this.loopHoleAckMessageProvider = loopHoleAckMessageProvider;
	}

    public void setFactory(LoopHoleFactory factory) {
        this.factory = factory;
    }

    @Override
    public void run() {
        if (!msg.getTo().equals(factory.getClientID())) {
            return;
        }
        
        LoopHoleAckMessage ackMsg = this.loopHoleAckMessageProvider.get();
        ackMsg.setLocalLoopID(msg.getClientLocalLoopID());
        ackMsg.setClientLocalLoopID(msg.getLocalLoopID());
        
        factory.getLoopHoleService(msg.getLocalLoopID()).send(ackMsg, msg.getSenderAddress());
    }
}
