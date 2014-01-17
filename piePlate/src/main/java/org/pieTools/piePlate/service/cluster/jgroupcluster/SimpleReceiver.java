package org.pieTools.piePlate.service.cluster.jgroupcluster;

import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.pieTools.piePlate.service.cluster.jgroupcluster.utility.MessageConverter;
import org.pieTools.piePlate.service.cluster.api.IClusterMessageHandler;
import org.pieTools.piePlate.service.cluster.jgroupcluster.api.IReceiver;

public class SimpleReceiver extends ReceiverAdapter implements IReceiver {

    private IClusterMessageHandler clusterMessageHandler;

    public SimpleReceiver() {
    }

    @Override
    public void setClusterMessageHandler(IClusterMessageHandler handler) {
        this.clusterMessageHandler = handler;
    }

    @Override
    public void receive(Message msg) {
        this.clusterMessageHandler.handleMessage(MessageConverter.convertMessageToPieMessage(msg));
    }

    @Override
    public void viewAccepted(View view) {

    }
}
