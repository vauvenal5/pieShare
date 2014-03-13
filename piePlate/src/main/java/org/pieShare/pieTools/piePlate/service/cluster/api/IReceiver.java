package org.pieShare.pieTools.piePlate.service.cluster.api;

import org.jgroups.Receiver;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public interface IReceiver extends Receiver {
    void setClusterService(IClusterService service);
}
