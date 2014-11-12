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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.NewFileMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.base.LocalFileEventTask;

/**
 *
 * @author Richard
 */
public class LocalFileCreatedTask extends LocalFileEventTask {

	private IFileService fileService;

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	@Override
	public void run() {
		try {
			PieFile pieFile = this.prepareWork();
			
			//todo: why do we scip directories?!
			if ((new File(this.filePath)).isDirectory()) {
				return;
			}
			
			NewFileMessage msg = beanService.getBean(PieShareAppBeanNames.getNewFileMessageName());
			
			this.historyService.syncPieFileWithDb(pieFile);
			
			super.doWork(msg, pieFile);
		} catch (IOException ex) {
			//todo: ex handling
		}
	}
}
