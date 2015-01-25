/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.task;

import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleAckMessage;
import org.pieShare.pieTools.piePlate.service.loophole.LoopHoleService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class LoopHoleAckTask implements IPieEventTask<LoopHoleAckMessage> {

    private LoopHoleAckMessage msg;
    private LoopHoleService loopHoleService;

    public void setLoopHoleService(LoopHoleService loopHoleService) {
        this.loopHoleService = loopHoleService;
    }

    @Override
    public void setEvent(LoopHoleAckMessage msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        PieLogger.info(this.getClass(), String.format("ACK Arrived from: %s", msg.getSenderID()));
        this.loopHoleService.ackArrived(msg.getSenderID());
    }

}
