package org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster;

import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.api.IReceiver;
import org.pieShare.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieShare.pieTools.piePlate.service.serializer.exception.SerializerServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorServiceException;

/**
 * Created by Svetoslav on 17.01.14.
 */
public class ObjectBasedReceiver extends ReceiverAdapter implements IReceiver {

    private ISerializerService serializerService;
    private IExecutorService executorService;

    public void setSerializerService(ISerializerService service) {
        this.serializerService = service;
    }

    @Override
    public void receive(Message msg) {
        try {
            IPieMessage pieMsg = this.serializerService.deserialize(msg.getBuffer());
            this.executorService.handlePieEvent(pieMsg);
        } catch (SerializerServiceException | PieExecutorServiceException e) {
            //todo-sv: fix error handling!
            e.printStackTrace();
        }
    }

    @Override
    public void viewAccepted(View view) {
        super.viewAccepted(view);
    }

    @Override
    public void setExecutorService(IExecutorService service) {
        this.executorService = service;
    }
}
