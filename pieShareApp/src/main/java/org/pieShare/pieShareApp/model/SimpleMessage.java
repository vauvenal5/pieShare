package org.pieShare.pieShareApp.model;

import org.pieShare.pieTools.piePlate.model.message.HeaderMessage;

/**
 * Created by vauvenal5 on 3/20/14.
 */
public class SimpleMessage extends HeaderMessage {
    private String msg;

    public SimpleMessage() {
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }
}
