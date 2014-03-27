package org.pieShare.pieTools.piePlate.service.helper;

import org.pieShare.pieTools.piePlate.service.helper.TestMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

/**
 * Created by Svetoslav on 19.01.14.
 */
public class TestTask implements IPieEventTask<TestMessage> {
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
