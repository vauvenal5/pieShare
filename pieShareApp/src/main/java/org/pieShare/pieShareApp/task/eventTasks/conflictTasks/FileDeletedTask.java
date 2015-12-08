/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.eventTasks.conflictTasks;

import org.pieShare.pieShareApp.model.message.api.IFileDeletedMessage;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.task.eventTasks.conflictTasks.ACheckConflictTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class FileDeletedTask extends ACheckConflictTask<IFileDeletedMessage>{
	
	private IFileService fileService;

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	@Override
	public void run() {
            PieLogger.trace(this.getClass(),"Deleting file: {}", this.msg.getPieFilder());
		if(!this.isConflictedOrNotNeeded((PieFile)this.msg.getPieFilder())) {
			this.fileService.deleteRecursive((PieFile) this.msg.getPieFilder());
		}
	}
	
}
