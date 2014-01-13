package org.pieTools.piePlate.service.cluster;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.pieTools.piePlate.service.cluster.api.*;
import org.pieTools.piePlate.service.exception.ClusterServiceException;
import org.pieTools.pieUtilities.beanService.IBeanService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClusterService implements IClusterService {

    private ExecutorService executor;

    private IBeanService beanService;
    private IChannelFactory channelFactory;
    private IReceiver receiver;

    private Map<String, JChannel> channels;

    public ClusterService() {
        this.executor = Executors.newCachedThreadPool();
        this.channels = new HashMap<>();
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
    public IClusterWrapper connect(String clusterName) throws ClusterServiceException {
        try {
            JChannel channel = this.channelFactory.getDefaultChannel();
            channel.setReceiver(this.receiver);
            channel.connect(clusterName);

            this.channels.put(clusterName, channel);
            DefaultClusterWrapper wrapper = new DefaultClusterWrapper();
            wrapper.setChannel(channel);
            return wrapper;
        } catch (Exception e) {
            throw new ClusterServiceException(e);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        IMessageTask task = (IMessageTask)beanService.getBean("messageTask");
        task.setMsg(MessageConverter.convertMessageToPieMessage(msg));
        this.executor.submit(task);
    }
}
