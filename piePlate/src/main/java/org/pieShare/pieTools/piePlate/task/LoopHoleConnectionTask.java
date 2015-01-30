/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.task;

import java.net.InetSocketAddress;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleAckMessage;
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
    private IBeanService beanService;
    private ILoopHoleFactory loopHoleFactory;

    @Override
    public void setEvent(LoopHoleConnectionMessage msg) {
        this.msg = msg;
    }

    public void setLoopHoleFactory(ILoopHoleFactory loopHoleFactory) {
        this.loopHoleFactory = loopHoleFactory;
    }

    public IBeanService getBeanService() {
        return beanService;
    }

    public void setBeanService(IBeanService beanService) {
        this.beanService = beanService;
    }

    public void setMsg(LoopHoleConnectionMessage msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        LoopHoleAckMessage ackMsg = beanService.getBean(LoopHoleAckMessage.class);
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

            LoopHolePunchMessage punchMsg = beanService.getBean(LoopHolePunchMessage.class);
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
            loopHoleService.newClientAvailable(address);
        }
    }
}
