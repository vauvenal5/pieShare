package org.pieTools.piePlate.service.cluster.jgroupcluster.api;

import org.jgroups.Receiver;
import org.pieTools.piePlate.service.cluster.api.IClusterMessageHandler;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public interface IReceiver extends Receiver {
    void setClusterMessageHandler(IClusterMessageHandler handler);
}
