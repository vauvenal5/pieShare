package org.pieShare.pieTools.piePlate.service.cluster;

import org.apache.commons.lang3.Validate;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterMessageHandler;
import org.pieShare.pieTools.piePlate.model.task.api.IMessageTask;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Svetoslav on 14.01.14.
 */
public class ClusterMessageHandler implements IClusterMessageHandler {
    private ExecutorService executor;
    private Map<Class, IMessageTask> tasks;

    public ClusterMessageHandler() {
        this.executor = Executors.newCachedThreadPool();
        this.tasks = new HashMap<>();
    }

    @Override
    public <P extends IPieMessage> void registerTask(Class<P> clazz, IMessageTask<P> task) {
        tasks.put(clazz, task);
    }

    @Override
    public void handleMessage(IPieMessage msg) {
        IMessageTask task = this.tasks.get(msg.getClass());

        Validate.notNull(task);

        task.setMsg(msg);
        this.executor.submit(task);
    }
}
