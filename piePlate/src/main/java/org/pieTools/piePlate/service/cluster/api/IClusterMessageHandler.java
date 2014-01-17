package org.pieTools.piePlate.service.cluster.api;

/**
 * Created by Svetoslav on 14.01.14.
 */
public interface IClusterMessageHandler {

    <P extends IPieMessage> void registerTask(Class<P> clazz, IMessageTask<P> task);

    void handleMessage(IPieMessage msg);
}
