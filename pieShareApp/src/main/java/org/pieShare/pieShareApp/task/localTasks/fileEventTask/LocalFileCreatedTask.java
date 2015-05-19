/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.fileEventTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.pieShare.pieShareApp.model.message.api.IFileCreatedMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class LocalFileCreatedTask extends ALocalFileEventTask {

	@Override
	public void run() {
		try {
			PieFile pieFile = this.prepareWork();
			
			if(pieFile == null) {
				return;
			}
			
			//todo: why do we scip directories?!
			if (pieFile == null || this.file.isDirectory()) {
				PieLogger.info(this.getClass(), "Sciping new file: {}", this.file.getName());
				return;
			}
			
			IFileCreatedMessage msg = this.messageFactoryService.getNewFileMessage();
			
			this.historyService.syncPieFileWithDb(pieFile);
			
			super.doWork(msg, pieFile);
		} catch (IOException ex) {
			//todo: ex handling
		}
	}
}
