/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks.conflictTasks;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.comparerService.api.ILocalFileCompareService;
import org.pieShare.pieShareApp.service.comparerService.exceptions.FileConflictException;
import org.pieShare.pieShareApp.service.fileFilterService.api.IFileFilterService;
import org.pieShare.pieTools.piePlate.model.message.api.IClusterMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.event.IPieEvent;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav Videnov
 */
public abstract class ACheckConflictTask<T extends IClusterMessage> extends PieEventTaskBase<T> {
	
	protected ILocalFileCompareService comparerService;
	protected IFileFilterService filterService;
	
	public void setComparerService(ILocalFileCompareService comparerService) {
		this.comparerService = comparerService;
	}
	
	public void setFilterService(IFileFilterService filterService){
		this.filterService = filterService;
	}
	
	protected boolean isConflictedOrNotNeeded(PieFile file) {
		return (this.comparerService.isConflictedOrNotNeeded(file)||!filterService.checkFile(file));
	}
	
	/*protected boolean isConflictedOrNotNeeded(PieFile file) {
		try {
			if(this.comparerService.compareToLocalPieFile(file) == -1) {
				PieLogger.info(this.getClass(), "Compared!");
				return false;
			}
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Compare failed!", ex);
		}
		
		PieLogger.info(this.getClass(), "Compared2!");
		
		return true;
	}*/
}
