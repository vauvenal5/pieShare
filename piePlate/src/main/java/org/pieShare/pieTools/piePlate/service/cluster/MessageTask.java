package org.pieShare.pieTools.piePlate.service.cluster;

import org.pieShare.pieTools.piePlate.dto.PieMessage;
import org.pieShare.pieTools.piePlate.service.cluster.api.IMessageTask;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public class MessageTask implements IMessageTask {

    private PieMessage msg;

    public MessageTask(){
    }

    @Override
    public void setMsg(PieMessage msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        System.out.println(new String(this.msg.getBuffer()));
        //org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger.info(this.getClass(), new String(this.msg.getBuffer()));
    }
}
