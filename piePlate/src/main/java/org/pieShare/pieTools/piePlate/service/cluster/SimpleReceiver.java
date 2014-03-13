package org.pieShare.pieTools.piePlate.service.cluster;

import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IReceiver;

public class SimpleReceiver extends ReceiverAdapter implements IReceiver {

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
