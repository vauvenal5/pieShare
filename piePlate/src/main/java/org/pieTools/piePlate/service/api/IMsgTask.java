package org.pieTools.piePlate.service.api;

import org.jgroups.Message;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public interface IMsgTask extends Runnable {
    void setMsg(Message msg);
}
