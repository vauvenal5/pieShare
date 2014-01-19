package org.pieTools.piePlate.service.cluster.api;

import org.pieTools.piePlate.dto.api.IMessageTask;
import org.pieTools.piePlate.dto.api.IPieMessage;

/**
 * Created by Svetoslav on 14.01.14.
 */
public interface IClusterMessageHandler {
    <P extends IPieMessage> void registerTask(Class<P> clazz, IMessageTask<P> task);

    void handleMessage(IPieMessage msg);
}
