/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.tasks;

import org.pieShare.pieShareServer.services.loopHoleService.api.ILoopHoleService;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleAckMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author RicLeo00
 */
public class LoopHoleServerAckTask implements IPieEventTask<LoopHoleAckMessage>{

    private LoopHoleAckMessage msg; 
    private ILoopHoleService loopHoleService;

    public void setLoopHoleService(ILoopHoleService loopHoleService) {
        this.loopHoleService = loopHoleService;
    }
    
    @Override
    public void setEvent(LoopHoleAckMessage msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        PieLogger.info(this.getClass(), String.format("ACK Arrived. ID: %s", msg.getSenderID()));
       loopHoleService.ackArrived(msg.getSenderID());
    }  
}
