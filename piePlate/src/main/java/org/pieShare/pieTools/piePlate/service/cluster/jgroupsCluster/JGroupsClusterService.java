package org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster;

import org.apache.commons.lang3.Validate;
import org.jgroups.JChannel;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.api.IReceiver;
import org.pieShare.pieTools.piePlate.service.serializer.api.ISerializerService;

public class JGroupsClusterService implements IClusterService {

    private IReceiver receiver;
    private ISerializerService serializerService;
    private JChannel channel;

    public JGroupsClusterService() {
    }

    public void setChannel(JChannel channel) {
        this.channel = channel;
    }

    public void setReceiver(IReceiver receiver) {
        this.receiver = receiver;
    }

    public void setSerializerService(ISerializerService service) {
        this.serializerService = service;
    }

    @Override
    public void connect(String clusterName) throws ClusterServiceException {
        try {
            Validate.notNull(this.receiver);

            this.channel.setReceiver(this.receiver);
            this.channel.setDiscardOwnMessages(true);
            this.channel.connect(clusterName);

        } catch (NullPointerException e) {
            throw new ClusterServiceException("Receiver not set!");
        } catch (Exception e) {
            throw new ClusterServiceException(e);
        }
    }

    @Override
    public void sendMessage(IPieMessage msg) throws ClusterServiceException {
        try {
            this.channel.send(null, this.serializerService.serialize(msg));
        } catch (Exception e) {
            throw new ClusterServiceException(e);
        }
    }

    @Override
    public int getMembersCount() {
        return this.channel.getView().getMembers().size();
    }

    @Override
    public boolean isConnectedToCluster() {
        return this.channel.isConnected();
    }
}
