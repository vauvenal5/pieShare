package org.pieShare.pieShareApp.model.task;

import org.pieShare.pieShareApp.model.SimpleMessage;
import org.pieShare.pieShareApp.model.action.SimpleMessageAction;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

/**
 * Created by vauvenal5 on 3/20/14.
 */
public class PrintTask<T extends SimpleMessage> implements IPieEventTask<T> {

    private SimpleMessage msg;

    @Override
    public void setMsg(SimpleMessage msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        System.out.println(this.msg.getMsg());
    }
}
