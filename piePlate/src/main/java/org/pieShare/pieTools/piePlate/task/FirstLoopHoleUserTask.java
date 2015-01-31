/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.task;

import jdk.nashorn.internal.runtime.regexp.JoniRegExp;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.FirstLoopHoleUserMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleAckMessage;
import org.pieShare.pieTools.piePlate.service.loophole.LoopHoleFactory;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;

/**
 *
 * @author RicLeo00
 */
public class FirstLoopHoleUserTask implements IPieEventTask<FirstLoopHoleUserMessage> {

    private FirstLoopHoleUserMessage msg;
    private LoopHoleFactory factory;
    private IBeanService beanService;

    public void setBeanService(IBeanService beanService) {
        this.beanService = beanService;
    }

    public void setFactory(LoopHoleFactory factory) {
        this.factory = factory;
    }

    @Override
    public void setEvent(FirstLoopHoleUserMessage msg) {
        this.msg = msg;
    }

    @Override
    public void run() {

        LoopHoleAckMessage ackMsg = beanService.getBean(LoopHoleAckMessage.class);
        ackMsg.setLocalLoopID(msg.getLocalLoopID());

        factory.getLoopHoleService(msg.getLocalLoopID()).sendToServer(msg);

        factory.getNewLoopHoleConnectionEvent().fireEvent(null);
    }

}
