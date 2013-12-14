package org.pieTools.piePlate.service.cluster.api;

import org.pieTools.piePlate.dto.PieMessage;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public interface IMessageTask extends Runnable {
    void setMsg(PieMessage msg);
}
