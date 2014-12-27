/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pieShareAppITs.helper.tasks;

import pieShareAppITs.helper.ITTasksCounter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorTaskFactoryException;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class TestTask extends PieEventTaskBase<IPieMessage> {
	
	private ITTasksCounter counter;
	private IPieExecutorTaskFactory factory;

	public void setUtil(ITTasksCounter counter) {
		this.counter = counter;
	}

	public void setFactory(IPieExecutorTaskFactory factory) {
		this.factory = factory;
	}

	@Override
	public void run() {
		try {
			IPieEventTask task = factory.getTask(this.msg);
			task.run();
			this.counter.increment(task.getClass());
		} catch (PieExecutorTaskFactoryException ex) {
			Logger.getLogger(TestTask.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
