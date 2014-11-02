/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package integrationTests.helper.tasks;

import integrationTests.helper.ITTasksCounter;
import integrationTests.helper.ITUtil;
import org.pieShare.pieShareApp.task.eventTasks.FileTransferCompleteTask;

/**
 *
 * @author Svetoslav
 */
public class FileTranserferCompleteTestTask extends FileTransferCompleteTask {
	
	ITTasksCounter counter;

	public void setUtil(ITTasksCounter counter) {
		this.counter = counter;
	}
	
	@Override
	public void run() {
		super.run();
		this.counter.increment(FileTransferCompleteTask.class);
	}
}
