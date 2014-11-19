/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.fileEventTask;

import java.io.IOException;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.message.FileChangedMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.fileService.fileListenerService.api.IFileWatcherService;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.base.LocalFileEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;


public class LocalFileChangedTask extends LocalFileEventTask {
	
	private IFileWatcherService fileWatcherService;

	public void setFileWatcherService(IFileWatcherService fileWatcherService) {
		this.fileWatcherService = fileWatcherService;
	}

	@Override
	public void run() {
		try {
			PieFile file = this.prepareWork();
			
			if(this.fileWatcherService.removePieFileFromModifiedList(file)) {
				PieLogger.info(this.getClass(), "Ignoring local file change because change was ours: {}", file.getRelativeFilePath());
				return;
			}
			
			this.historyService.syncPieFileWithDb(file);
			
			FileChangedMessage msg = beanService.getBean(PieShareAppBeanNames.getFileChangedMessage());
			
			super.doWork(msg, file);
		} catch (IOException ex) {
			PieLogger.info(this.getClass(), "Local file delete messed up!", ex);
		}
	}

}
