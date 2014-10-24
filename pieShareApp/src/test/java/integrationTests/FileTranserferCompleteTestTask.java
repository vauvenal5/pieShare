/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package integrationTests;

import org.pieShare.pieShareApp.task.eventTasks.FileTransferCompleteTask;

/**
 *
 * @author Svetoslav
 */
public class FileTranserferCompleteTestTask extends FileTransferCompleteTask {
	
	IntegrationTestUtil util;

	public void setUtil(IntegrationTestUtil util) {
		this.util = util;
	}
	
	@Override
	public void run() {
		super.run();
		util.fileTransferCompletedTaskCompleted();
	}
}
