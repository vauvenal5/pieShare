package org.pieTools.piePlate.service.integrationTests;

import org.pieTools.piePlate.model.task.api.IMessageTask;

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
