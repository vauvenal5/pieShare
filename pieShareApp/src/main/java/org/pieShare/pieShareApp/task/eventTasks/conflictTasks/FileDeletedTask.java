/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.eventTasks.conflictTasks;

import org.pieShare.pieShareApp.model.message.fileHistoryMessage.FileDeletedMessage;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class FileDeletedTask extends ACheckConflictTask<FileDeletedMessage>{
	
	private IFileService fileService;

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	@Override
	public void run() {
            PieLogger.trace(this.getClass(),"Deleting file: {}", this.msg.getPieFolder());
		if(!this.isConflictedOrNotNeeded((PieFile)this.msg.getPieFolder())) {
			this.fileService.deleteRecursive((PieFile) this.msg.getPieFolder());
		}
	}
	
}
