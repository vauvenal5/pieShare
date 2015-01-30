/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.task;

import jdk.nashorn.internal.runtime.regexp.JoniRegExp;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleCompleteMessage;
import org.pieShare.pieTools.piePlate.service.loophole.api.ILoopHoleFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;

/**
 *
 * @author RicLeo00
 */
public class LoopHoleCompleteTask implements IPieEventTask<LoopHoleCompleteMessage> {

    private LoopHoleCompleteMessage msg;
    private ILoopHoleFactory loopholeFactory;

    public void setLoopholeFactory(ILoopHoleFactory loopholeFactory) {
        this.loopholeFactory = loopholeFactory;
    }

    @Override
    public void setEvent(LoopHoleCompleteMessage msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
       loopholeFactory.getLoopHoleService(msg.getLocalLoopID()).clientCompletedLoopHole();
    }

}
