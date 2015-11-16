/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.task;

import java.net.InetSocketAddress;
import javax.inject.Provider;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleAckMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleCompleteMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleConnectionMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHolePunchMessage;
import org.pieShare.pieTools.piePlate.service.loophole.api.ILoopHoleFactory;
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
    private Provider<LoopHoleAckMessage> loopHoleAckMessageProvider;
	private Provider<LoopHolePunchMessage> loopHolePunchMessageProvider;
	private Provider<LoopHoleCompleteMessage> loopHoleCompleteMessageProvider;
    private ILoopHoleFactory loopHoleFactory;

    @Override
    public void setEvent(LoopHoleConnectionMessage msg) {
        this.msg = msg;
    }

    public void setLoopHoleFactory(ILoopHoleFactory loopHoleFactory) {
        this.loopHoleFactory = loopHoleFactory;
    }

    public void setMsg(LoopHoleConnectionMessage msg) {
        this.msg = msg;
    }

	public void setLoopHoleAckMessageProvider(Provider<LoopHoleAckMessage> loopHoleAckMessageProvider) {
		this.loopHoleAckMessageProvider = loopHoleAckMessageProvider;
	}

	public void setLoopHolePunchMessageProvider(Provider<LoopHolePunchMessage> loopHolePunchMessageProvider) {
		this.loopHolePunchMessageProvider = loopHolePunchMessageProvider;
	}

	public void setLoopHoleCompleteMessageProvider(Provider<LoopHoleCompleteMessage> loopHoleCompleteMessageProvider) {
		this.loopHoleCompleteMessageProvider = loopHoleCompleteMessageProvider;
	}

    @Override
    public void run() {
        LoopHoleAckMessage ackMsg = loopHoleAckMessageProvider.get();
        ackMsg.setLocalLoopID(msg.getLocalLoopID());

        loopHoleService = loopHoleFactory.getLoopHoleService(msg.getLocalLoopID());
        loopHoleService.sendToServer(ackMsg);
        int endpoint = 0;

        while (!stop) {

            if (endpoint == 0) {
                host = msg.getClientPrivateIP();
                port = msg.getClientPrivatePort();
                endpoint++;
            } else {
                host = msg.getClientPublicIP();
                port = msg.getClientPublicPort();
                endpoint = 0;
            }

            LoopHolePunchMessage punchMsg = loopHolePunchMessageProvider.get();
            punchMsg.setTo(msg.getFromId());
            punchMsg.setFrom(loopHoleFactory.getClientID());
            punchMsg.setName(loopHoleService.getName());
            punchMsg.setLocalLoopID(msg.getClientLocalLoopID());
            punchMsg.setClientLocalLoopID(msg.getLocalLoopID());

            loopHoleService.addInWaitFromAckQueu(msg.getFromId(), this);
            loopHoleService.send(punchMsg, new InetSocketAddress(host, port));

            isWaitingForAck = true;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                PieLogger.error(this.getClass(), "Error while waiting for ACK", ex);
            }
            isWaitingForAck = false;
            loopHoleService.removeTaskFromAckWaitQueue(msg.getFromId());
        }
    }

    public void ackArrived() {
        if (isWaitingForAck) {
            stop = true;
            InetSocketAddress address = new InetSocketAddress(host, port); 
            
            LoopHoleCompleteMessage completeMessage = loopHoleCompleteMessageProvider.get();
            completeMessage.setLocalLoopID(msg.getClientLocalLoopID());
            completeMessage.setClientLocalLoopID(msg.getLocalLoopID());
            loopHoleService.send(completeMessage, address);
            
            loopHoleService.newClientAvailable(address);
        }
    }
}
