/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadTest.loadTestLib.task;

import loadTest.loadTestLib.helper.LFileComparer;
import loadTest.loadTestLib.message.AllFilesCompleteMessage;
import loadTest.loadTestLib.message.ClientIsUpMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import pieShareAppITs.helper.ITTasksCounter;

/**
 *
 * @author richy
 */
public class ClientIsUpTask implements IPieEventTask<ClientIsUpMessage> {

    private ClientIsUpMessage msg;
    private ITTasksCounter counter;
    
    public void setTaskCounter(ITTasksCounter counter) {
        this.counter = counter;     
    }

    @Override
    public void setEvent(ClientIsUpMessage msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        counter.increment(this.getClass());
		PieLogger.info(this.getClass(), "Slave is up!");
    }

}
