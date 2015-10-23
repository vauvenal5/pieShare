/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTestTools;

import loadTest.loadTestLib.message.ClientIsUpMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.event.IPieEvent;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import pieShareAppITs.helper.ITTasksCounter;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public abstract class AMessageCounterTask<P extends IPieEvent> implements IPieEventTask<P> {
	private P msg;
    private ITTasksCounter counter;
    
    public void setTaskCounter(ITTasksCounter counter) {
        this.counter = counter;     
    }

	@Override
    public void setEvent(P msg) {
        this.msg = msg;
    }

	@Override
    public void run() {
        counter.increment(msg.getClass());
    }
}
