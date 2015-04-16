/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTest.loadTestLib.task;

import loadTest.loadTestLib.message.AllFilesCompleteMessage;
import org.jgroups.protocols.COUNTER;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import pieShareAppITs.helper.ITTasksCounter;

/**
 *
 * @author richy
 */
public class AllFilesCompleteTask implements IPieEventTask<AllFilesCompleteMessage> {

    private AllFilesCompleteMessage msg;
    private ITTasksCounter counter;

    public void setTaskCounter(ITTasksCounter counter) {
        this.counter = counter;     
    }

    @Override
    public void setEvent(AllFilesCompleteMessage msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        counter.increment(this.getClass());
    }

}
