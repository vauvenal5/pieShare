package org.pieShare.pieTools.piePlate.service.helper;

import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;

/**
 * Created by Svetoslav on 19.01.14.
 */
public class TestMessage implements IPieMessage {
    String msg;
    String type;

    public TestMessage() {
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
}
