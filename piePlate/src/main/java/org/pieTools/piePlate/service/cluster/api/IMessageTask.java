package org.pieTools.piePlate.service.cluster.api;

import org.jgroups.Message;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public interface IMessageTask extends Runnable {
    void setMsg(Message msg);
}
