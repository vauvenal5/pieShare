package org.pieTools.piePlate.service.cluster;

import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.pieTools.piePlate.service.api.IClusterService;
import org.pieTools.piePlate.service.api.IRecivier;

public class SimpleReceiver extends ReceiverAdapter implements IRecivier {

    private IClusterService service;

    public SimpleReceiver() {
    }

    @Override
    public void setClusterService(IClusterService service) {
        this.service = service;
    }

    public void receive(Message msg) {
        this.service.handleMessage(msg);
    }
}
