/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.tasks;

import java.net.InetSocketAddress;
import org.pieShare.pieShareServer.services.loopHoleService.api.ILoopHoleService;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author RicLeo00
 */
public class WaitForAckFromClientTask implements IPieTask {

    private IUdpMessage msgToSend;
    private InetSocketAddress udpAddress;
    private boolean ackArrived = false;
    private ILoopHoleService loopHoleService;

    public void setUdpAddress(InetSocketAddress udpAddress) {
        this.udpAddress = udpAddress;
    }

    public void setLoopHoleService(ILoopHoleService loopHoleService) {
        this.loopHoleService = loopHoleService;
    }

    public IUdpMessage getMmsgToSend() {
        return msgToSend;
    }

    public void setMmsgToSend(IUdpMessage msgToSend) {
        this.msgToSend = msgToSend;
    }

    @Override
    public void run() {

        try {
            PieLogger.info(this.getClass(), String.format("WaitForAck. ID: %s", msgToSend.getSenderID()));
            Thread.sleep(20000);
        } catch (InterruptedException ex) {
            PieLogger.debug(this.getClass(), "Error while thread sleep.", ex);
        }

        if (!ackArrived) {
            loopHoleService.send(msgToSend, udpAddress);
        }
    }

    public void ackArrived() {
        this.ackArrived = true;
    }

}
