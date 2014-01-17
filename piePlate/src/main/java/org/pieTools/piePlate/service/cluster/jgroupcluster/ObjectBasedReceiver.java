package org.pieTools.piePlate.service.cluster.jgroupcluster;

import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;
import org.pieTools.piePlate.service.cluster.api.IClusterMessageHandler;
import org.pieTools.piePlate.service.cluster.api.IPieMessage;
import org.pieTools.piePlate.service.cluster.jgroupcluster.api.IReceiver;

/**
 * Created by Svetoslav on 17.01.14.
 */
public class ObjectBasedReceiver extends ReceiverAdapter implements IReceiver{

    private IClusterMessageHandler messageHandler;

    @Override
    public void setClusterMessageHandler(IClusterMessageHandler handler) {
        this.messageHandler = handler;
    }

    @Override
    public void receive(Message msg) {
        try {
            IPieMessage pieMsg = (IPieMessage)Util.objectFromByteBuffer(msg.getBuffer());
            this.messageHandler.handleMessage(pieMsg);
        } catch (Exception e) {
            //todo-sv: fix error handling!
            e.printStackTrace();
        }
    }

    @Override
    public void viewAccepted(View view) {
        super.viewAccepted(view);
    }
}
