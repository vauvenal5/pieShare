package org.pieShare.pieShareApp.model.task;

import org.pieShare.pieShareApp.model.SimpleMessage;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.model.task.api.IMessageTask;

/**
 * Created by vauvenal5 on 3/20/14.
 */
public class PrintTask implements IMessageTask {

    private SimpleMessage msg;

    @Override
    public void setMsg(IPieMessage msg) {
        this.msg = (SimpleMessage)msg;
    }

    @Override
    public void run() {
        System.out.println(this.msg.getMsg());
    }
}
