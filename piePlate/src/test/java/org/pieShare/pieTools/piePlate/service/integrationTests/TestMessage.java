package org.pieShare.pieTools.piePlate.service.integrationTests;

import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;

/**
 * Created by Svetoslav on 19.01.14.
 */
public class TestMessage implements IPieMessage {
    String msg;

    public TestMessage() {
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }
}
