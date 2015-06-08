/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTest.loadTestLib.task;

import loadTest.loadTestLib.helper.LFileComparer;
import loadTest.loadTestLib.message.AllFilesCompleteMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import pieShareAppITs.helper.ITTasksCounter;

/**
 *
 * @author richy
 */
public class AllFilesCompleteTask implements IPieEventTask<AllFilesCompleteMessage> {

    private AllFilesCompleteMessage msg;
    private ITTasksCounter counter;
    private LFileComparer comparer;
    
    public void setTaskCounter(ITTasksCounter counter) {
        this.counter = counter;     
    }

    public void setComparer(LFileComparer comparer) {
        this.comparer = comparer;
    }
    
    @Override
    public void setEvent(AllFilesCompleteMessage msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        comparer.comarare(msg.getFiles());
        counter.increment(this.getClass());
		PieLogger.info(this.getClass(), "Slave done!");
    }

}
