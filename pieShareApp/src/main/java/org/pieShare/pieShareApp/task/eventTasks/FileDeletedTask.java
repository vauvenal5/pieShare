/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.eventTasks;

import org.pieShare.pieShareApp.model.message.FileDeletedMessage;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class FileDeletedTask extends PieEventTaskBase<FileDeletedMessage>{
	
	private IFileService fileService;

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	@Override
	public void run() {
		this.fileService.deleteRecursive(this.msg.getFile());
	}
	
}
