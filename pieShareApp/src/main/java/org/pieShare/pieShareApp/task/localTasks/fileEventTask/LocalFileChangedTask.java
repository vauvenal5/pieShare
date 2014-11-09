/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.fileEventTask;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.message.FileChangedMessage;
import org.pieShare.pieShareApp.service.fileListenerService.api.IFileListenerService;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.base.FileHistoryEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;


public class LocalFileChangedTask extends FileHistoryEventTask {

	private IFileService fileService;
	private IFileListenerService fileListener;

	public void setFileListener(IFileListenerService fileListener) {
		this.fileListener = fileListener;
	}

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	@Override
	public void run() {
		try {
			PieFile file = this.fileUtilsService.getPieFile(this.filePath);
			
			if(!checkFilter(file)) return;
			
			if(this.fileListener.removePieFileFromModifiedList(file)) {
				PieLogger.info(this.getClass(), "Ignoring local file change because change was ours: {}", file.getRelativeFilePath());
				return;
			}
			
			this.fileService.waitUntilCopyFinished(this.filePath);
			
			//todo: for the time being we will just delete without checks
			//later somekinde of persistency and check has to be added
			//see base class of changedMessage
			FileChangedMessage msg = beanService.getBean(PieShareAppBeanNames.getFileChangedMessage());
			
			super.doWork(msg);
		} catch (IOException ex) {
			PieLogger.info(this.getClass(), "Local file delete messed up!", ex);
		}
	}

}
