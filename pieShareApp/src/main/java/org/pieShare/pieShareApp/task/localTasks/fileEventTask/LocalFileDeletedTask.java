/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.localTasks.fileEventTask;

import java.io.IOException;
import org.pieShare.pieShareApp.model.message.api.IFileDeletedMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.base.LocalFileEventTask;

/**
 *
 * @author Svetoslav
 */
public class LocalFileDeletedTask extends LocalFileEventTask {

	@Override
	public void run() {
		try {
			PieFile pieFile = this.prepareWork();
			pieFile = this.historyService.syncDeleteToHistory(pieFile);
			IFileDeletedMessage msg = this.messageFactoryService.getFileDeletedMessage();
			super.doWork(msg, pieFile);
		} catch (IOException ex) {
			//todo: do something here
		}
	}
	
}
