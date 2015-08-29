/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.comparerService;

import java.io.IOException;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.comparerService.api.ILocalFileCompareService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public abstract class ALocalFileCompareService implements ILocalFileCompareService{
	
	private ALocalFileCompareService wrappedCompareService;

	public void setWrappedCompareService(ALocalFileCompareService wrappedCompareService) {
		this.wrappedCompareService = wrappedCompareService;
	}
	
	protected abstract PieFile getLocalPieFile(PieFile remoteFile) throws NullPointerException, IOException;
	
	protected boolean equalsWithLocal(PieFile remoteFile) throws NullPointerException, IOException {
		try {
			if(this.wrappedCompareService != null) {
				return this.wrappedCompareService.equalsWithLocal(remoteFile);
			}
		}
		catch(NullPointerException ex) {
			//do nothing
		}
		
		PieFile local = this.getLocalPieFile(remoteFile);
		return local.equals(remoteFile);
	}
	
	@Override
	public boolean equalsWithLocalPieFile(PieFile remoteFile) {
		try {
			return this.equalsWithLocal(remoteFile);
		}
		catch(NullPointerException | IOException ex) {
			//do nothing
		}
		
		return false;
	}
	
	protected int compareToLocal(PieFile remoteFile) throws NullPointerException, IOException {
		try {
			if(this.wrappedCompareService != null) {
				return this.wrappedCompareService.compareToLocal(remoteFile);
			}
		}
		catch(NullPointerException ex) {
			//do nothing
		}
		
		PieFile local= this.getLocalPieFile(remoteFile);
		return local.compareTo(remoteFile);
	}
	
	@Override
	public int compareToLocalPieFile(PieFile remoteFile) {
		try {
			return this.compareToLocal(remoteFile);
		}
		catch(NullPointerException | IOException ex) {
			return -1;
		}
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
}
