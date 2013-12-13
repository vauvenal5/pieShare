package org.pieTools.piePlate.service.cluster;

import org.jgroups.Message;
import org.pieTools.piePlate.service.cluster.api.IMessageTask;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public class MessageTask implements IMessageTask {

    private Message msg;

    public MessageTask(){
    }

    @Override
    public void setMsg(Message msg) {
        this.msg = msg;
    }

    @Override
    public void run() {

    }
}
