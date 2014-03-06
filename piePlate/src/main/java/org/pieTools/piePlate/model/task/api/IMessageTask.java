package org.pieTools.piePlate.model.task.api;

import org.pieTools.piePlate.model.message.api.IPieMessage;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public interface IMessageTask<P extends IPieMessage> extends Runnable {
    void setMsg(P msg);
}
