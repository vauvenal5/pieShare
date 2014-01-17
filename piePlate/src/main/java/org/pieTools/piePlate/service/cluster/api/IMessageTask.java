package org.pieTools.piePlate.service.cluster.api;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public interface IMessageTask<P extends IPieMessage> extends Runnable {
    void setMsg(P msg);
}
