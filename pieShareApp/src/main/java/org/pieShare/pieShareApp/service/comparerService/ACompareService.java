/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.comparerService;

import java.io.IOException;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieShareApp.service.comparerService.api.ICompareService;

/**
 *
 * @author Richard
 */
public abstract class ACompareService implements ICompareService{
	
	//this is a decorater pattern because of the following scenario
	//the local state should always win because we can not guarantee that
	//the DB will be already consistent with the local state at time of event
	//however if the local state returns null we should also check with the DB 
	//if an event is needed or not
	protected ICompareService decoratedCompareService;
	
	protected abstract PieFile getPieFile(PieFile file);
	protected abstract PieFolder getPieFolder(PieFolder folder);

	public void setDecoratedCompareService(ICompareService decoratedCompareService) {
		this.decoratedCompareService = decoratedCompareService;
	}
	
	@Override
	public boolean equalsWithLocalPieFile(PieFile remoteFile) {
		PieFile local = this.getPieFile(remoteFile);
		return local != null && local.equals(remoteFile);
	}
	
	@Override
	public int compareToLocalPieFile(PieFile remoteFile) {
		PieFile local = this.getPieFile(remoteFile);
		
		if(local == null) {
			if(this.decoratedCompareService == null) {
				return -1;
			}
			
			return this.decoratedCompareService.compareToLocalPieFile(remoteFile);
		}
		
		return local.compareTo(remoteFile);
	}
	
	@Override
	public boolean isConflictedOrNotNeeded(PieFile file) {
		if(this.compareToLocalPieFile(file) == -1) {
			PieLogger.info(this.getClass(), "Compared false!");
			return false;
		}
		
		PieLogger.info(this.getClass(), "Compared true!");
		
		return true;
	}
	
	@Override
	public int compareToLocalPieFolder(PieFolder remoteFolder) {
		PieFolder local = this.getPieFolder(remoteFolder);
		
		if(local == null) {
			if(this.decoratedCompareService == null) {
				return -1;
			}
			
			return this.decoratedCompareService.compareToLocalPieFolder(remoteFolder);
		}
		
		return local.compareTo(remoteFolder);
	}

	@Override
	public boolean isConflictedOrNotNeeded(PieFolder folder) {
		if(this.compareToLocalPieFolder(folder) == -1) {
			return false;
		}
		return true;
	}
}
