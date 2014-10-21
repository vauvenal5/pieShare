/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks;

import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.message.FileChangedMessage;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.task.localTasks.base.FileHistoryEventTask;


public class LocalFileChangedTask extends FileHistoryEventTask {

	private IFileService fileService;

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	@Override
	public void run() {
		this.fileService.waitUntilCopyFinished(this.filePath);
		
		//todo: for the time being we will just delete without checks
		//later somekinde of persistency and check has to be added
		//see base class of changedMessage
		FileChangedMessage msg = beanService.getBean(PieShareAppBeanNames.getFileChangedMessage());
		
		super.doWork(msg);
	}

}
