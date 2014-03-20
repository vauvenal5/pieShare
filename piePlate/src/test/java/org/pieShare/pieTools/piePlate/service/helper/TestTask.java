package org.pieShare.pieTools.piePlate.service.helper;

import org.pieShare.pieTools.piePlate.model.task.api.IMessageTask;
import org.pieShare.pieTools.piePlate.service.helper.TestMessage;

/**
 * Created by Svetoslav on 19.01.14.
 */
public class TestTask implements IMessageTask<TestMessage> {
    private TestMessage msg;
    private boolean run = false;

    @Override
    public void setMsg(TestMessage msg) {
        this.msg = msg;
    }

    public TestMessage getMsg() {
        return this.msg;
    }

    public boolean getRun() {
        return this.run;
    }

    @Override
    public void run() {
        this.run = true;
    }
}
