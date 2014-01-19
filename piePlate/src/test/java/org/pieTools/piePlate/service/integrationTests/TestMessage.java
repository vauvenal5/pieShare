package org.pieTools.piePlate.service.integrationTests;

import org.pieTools.piePlate.dto.api.IPieMessage;

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
