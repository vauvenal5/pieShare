package org.pieTools.piePlate.service.cluster;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.pieTools.piePlate.service.api.IClusterService;
import org.pieTools.piePlate.service.api.IMsgTask;
import org.pieTools.piePlate.service.api.IRecivier;
import org.pieTools.piePlate.service.exception.ClusterServiceException;
import org.pieTools.pieUtilities.beanService.IBeanService;

import java.util.concurrent.ExecutorService;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public class ClusterService implements IClusterService {

    private ExecutorService executor;
    private JChannel channel;
    private IBeanService beanService;

    public void setBeanService(IBeanService service) {
        this.beanService = service;
    }

    @Override
    public void connect(String clusterName) throws ClusterServiceException {
        try {
            channel.setReceiver((IRecivier)beanService.getBean("simpleReceiver"));
            channel.connect(clusterName);
        } catch (Exception e) {
            throw new ClusterServiceException(e);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        IMsgTask task = (IMsgTask)beanService.getBean("msgTask");
        task.setMsg(msg);
        this.executor.submit(task);
    }
}
