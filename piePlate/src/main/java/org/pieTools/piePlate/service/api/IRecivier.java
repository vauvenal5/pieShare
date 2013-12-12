package org.pieTools.piePlate.service.api;

import org.jgroups.Receiver;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public interface IRecivier extends Receiver {
    void setClusterService(IClusterService service);
}
