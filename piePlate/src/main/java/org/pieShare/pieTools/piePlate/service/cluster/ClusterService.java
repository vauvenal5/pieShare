package org.pieShare.pieTools.piePlate.service.cluster;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.pieShare.pieTools.piePlate.dto.PieMessage;
import org.pieShare.pieTools.piePlate.service.cluster.api.IChannelFactory;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IMessageTask;
import org.pieShare.pieTools.piePlate.service.cluster.api.IReceiver;
import org.pieShare.pieTools.piePlate.service.exception.ClusterServiceException;
import org.pieShare.pieTools.pieUtilities.beanService.IBeanService;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClusterService implements IClusterService {

    private ExecutorService executor;
    private JChannel channel;

    private IBeanService beanService;
    private IChannelFactory channelFactory;
    private IReceiver receiver;

    public ClusterService() {
        this.executor = Executors.newCachedThreadPool();
    }

    @PostConstruct
    private void postClusterService() {
        this.receiver.setClusterService(this);
    }

    public void setBeanService(IBeanService service) {
        this.beanService = service;
    }

    public void setChannelFactory(IChannelFactory channelFactory) {
        this.channelFactory = channelFactory;
    }

    public void setReceiver(IReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void connect(String clusterName) throws ClusterServiceException {
        try {
            this.channel = this.channelFactory.getDefaultChannel();
            this.channel.setReceiver(this.receiver);
            this.channel.connect(clusterName);
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

    @Override
    public void handleMessage(Message msg) {
        IMessageTask task = (IMessageTask)beanService.getBean("messageTask");
        task.setMsg(MessageConverter.convertMessageToPieMessage(msg));
        this.executor.submit(task);
    }

    @Override
    public void sendMessage(PieMessage msg) throws ClusterServiceException {
        try {
            this.channel.send(null, msg.getBuffer());
        } catch (Exception e) {
            throw new ClusterServiceException(e);
        }
    }
}
