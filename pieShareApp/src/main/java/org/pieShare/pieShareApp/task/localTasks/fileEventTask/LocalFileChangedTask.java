/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.fileEventTask;

import java.io.IOException;
import org.pieShare.pieShareApp.model.message.api.IFileChangedMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;


public class LocalFileChangedTask extends ALocalFileEventTask {
	
	

	@Override
	public void run() {
		try {
			PieFile file = this.prepareWork();
			
			if(file == null) {
				PieLogger.info(this.getClass(), "Ignoring local file change because change was ours: {}", this.file.getAbsolutePath());
				return;
			}
			
			this.historyService.syncPieFileWithDb(file);
			
			IFileChangedMessage msg = this.messageFactoryService.getFileChangedMessage();
			
			super.doWork(msg, file);
		} catch (IOException ex) {
			PieLogger.info(this.getClass(), "Local file delete messed up!", ex);
		}
	}

}
