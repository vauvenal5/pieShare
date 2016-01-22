/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks.conflictTasks;

import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieTools.piePlate.model.message.api.IClusterMessage;

/**
 *
 * @author Svetoslav Videnov
 */
public abstract class ARequestTask<T extends IClusterMessage> extends ACheckConflictTask<T> {
	
	protected IRequestService requestService;

    public void setRequestService(IRequestService requestService) {
        this.requestService = requestService;
    }
	
	protected void doWork(PieFile file) {
		if(!this.isConflictedOrNotNeeded(file)) {
			this.requestService.requestFile(file);
		}
	}
}
