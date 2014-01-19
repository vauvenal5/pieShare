package org.pieTools.piePlate.service.cluster.jgroupcluster;

import org.apache.commons.lang3.Validate;
import org.jgroups.JChannel;
import org.pieTools.piePlate.service.cluster.api.IClusterMessageHandler;
import org.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieTools.piePlate.model.task.api.IMessageTask;
import org.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieTools.piePlate.service.cluster.jgroupcluster.api.IChannelFactory;
import org.pieTools.piePlate.service.cluster.jgroupcluster.api.IReceiver;
import org.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieTools.piePlate.service.serializer.api.ISerializerService;

import javax.annotation.PostConstruct;

public class JGroupsClusterService implements IClusterService {

    private IChannelFactory channelFactory;
    private IReceiver receiver;
    private IClusterMessageHandler clusterMessageHandler;
    private ISerializerService serializerService;

    private JChannel channel;

    public JGroupsClusterService() {
    }

    @PostConstruct
    private void postClusterService() {
    }

    public void setChannelFactory(IChannelFactory channelFactory) {
        this.channelFactory = channelFactory;
    }

    public void setReceiver(IReceiver receiver) {
        this.receiver = receiver;
    }

    public void setClusterMessageHandler(IClusterMessageHandler handler) {
        this.clusterMessageHandler = handler;
    }

    public void setSerializerService(ISerializerService service) {
        this.serializerService = service;
    }

    @Override
    public <P extends IPieMessage> void registerTask(Class<P> clazz, IMessageTask<P> task) {
        Validate.notNull(this.clusterMessageHandler);
        this.clusterMessageHandler.registerTask(clazz, task);
    }

    @Override
    public void connect(String clusterName) throws ClusterServiceException {
        try {
            Validate.notNull(this.receiver);

            this.channel = this.channelFactory.getDefaultChannel();
            this.channel.setReceiver(this.receiver);
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
