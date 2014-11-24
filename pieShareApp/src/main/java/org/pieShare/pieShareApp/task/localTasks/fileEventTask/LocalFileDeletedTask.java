/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.localTasks.fileEventTask;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.message.FileDeletedMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.historyService.IHistoryService;
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
			FileDeletedMessage msg = this.beanService.getBean(PieShareAppBeanNames.getFileDeletedMessage());
			super.doWork(msg, pieFile);
		} catch (IOException ex) {
			//todo: do something here
		}
	}
	
}
