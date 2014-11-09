/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.localTasks;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.service.comparerService.api.IComparerService;
import org.pieShare.pieShareApp.service.comparerService.exceptions.FileConflictException;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class ComparePieFileTask implements IPieTask {
	
	private PieFile pieFile;
	private IComparerService comparerService;
	private IRequestService requestService;
	
	public void setRequestService(IRequestService requestService) {
		this.requestService = requestService;
	}

	public void setComparerService(IComparerService comparerService) {
		this.comparerService = comparerService;
	}
	
	public void setPieFile(PieFile file) {
		this.pieFile = file;
	}

	@Override
	public void run() {
		try {
			switch(this.comparerService.comparePieFile(pieFile)) {
				case 1:
					this.requestService.requestFile(pieFile);
					break;
				case -1:
					//todo: conflict situation!!! user has to decide!
					break;
				default:
					break;
			}
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Compare Task error.", ex);
		} catch (FileConflictException ex) {
			PieLogger.error(this.getClass(), "Compare Task error.", ex);
		}
	}
}
