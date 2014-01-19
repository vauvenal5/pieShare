package org.pieTools.piePlate.service.cluster.jgroupcluster;

import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.pieTools.piePlate.service.cluster.api.IClusterMessageHandler;
import org.pieTools.piePlate.dto.api.IPieMessage;
import org.pieTools.piePlate.service.cluster.jgroupcluster.api.IReceiver;
import org.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieTools.piePlate.service.serializer.exception.SerializerServiceException;

/**
 * Created by Svetoslav on 17.01.14.
 */
public class ObjectBasedReceiver extends ReceiverAdapter implements IReceiver{

    private IClusterMessageHandler messageHandler;
    private ISerializerService serializerService;


    @Override
    public void setClusterMessageHandler(IClusterMessageHandler handler) {
        this.messageHandler = handler;
    }

    public void setSerializerService(ISerializerService service) {
        this.serializerService = service;
    }

    @Override
    public void receive(Message msg) {
        try {
            IPieMessage pieMsg = this.serializerService.deserialize(msg.getBuffer());
            this.messageHandler.handleMessage(pieMsg);
        } catch (SerializerServiceException e) {
            //todo-sv: fix error handling!
            e.printStackTrace();
        }
    }

    @Override
    public void viewAccepted(View view) {
        super.viewAccepted(view);
    }
}
