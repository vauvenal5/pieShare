/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.comparerService;

import java.io.IOException;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.comparerService.api.ILocalFileCompareService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public abstract class ALocalFileCompareService implements ILocalFileCompareService{
	
	protected abstract PieFile getPieFile(PieFile file);
	protected abstract PieFolder getPieFolder(PieFolder folder);
	
	@Override
	public boolean equalsWithLocalPieFile(PieFile remoteFile) {
		PieFile local = this.getPieFile(remoteFile);
		return local != null && local.equals(remoteFile);
	}
	
	@Override
	public int compareToLocalPieFile(PieFile remoteFile) {
		PieFile local = this.getPieFile(remoteFile);
		
		if(local == null) {
			return -1;
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
			return -1;
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
