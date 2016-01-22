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
		//todo-mr3: delete has to compare with the previous file!!!
		//todo-mr3: modified by us?
		//todo-mr3: sync to DB here or over the local file event?
            PieLogger.trace(this.getClass(),"Deleting file: {}", this.msg.getPieFilder());
		if(!this.isConflictedOrNotNeeded((PieFile)this.msg.getPieFilder())) {
			this.fileService.deleteRecursive((PieFile) this.msg.getPieFilder());
		}
	}
	
}
