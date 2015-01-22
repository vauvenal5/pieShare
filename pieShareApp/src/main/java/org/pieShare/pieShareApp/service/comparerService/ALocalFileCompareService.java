/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.comparerService;

import java.io.IOException;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.comparerService.api.ILocalFileCompareService;

/**
 *
 * @author Richard
 */
public abstract class ALocalFileCompareService implements ILocalFileCompareService{
	
	private ALocalFileCompareService wrappedCompareService;

	public void setWrappedCompareService(ALocalFileCompareService wrappedCompareService) {
		this.wrappedCompareService = wrappedCompareService;
	}
	
	protected abstract PieFile getLocalPieFile(PieFile remoteFile) throws IOException;
	
	protected boolean equalsWithLocal(PieFile remoteFile) throws IOException {
		try {
			if(this.wrappedCompareService != null) {
				return this.wrappedCompareService.equalsWithLocal(remoteFile);
			}
		}
		catch(IOException ex) {
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
		catch(IOException ex) {
			//do nothing
		}
		
		return false;
	}
	
	@Override
	public int compareToLocalPieFile(PieFile remoteFile) throws IOException {
		try {
			if(this.wrappedCompareService != null) {
				return this.wrappedCompareService.compareToLocalPieFile(remoteFile);
			}
		}
		catch(IOException ex) {
			//do nothing
		}
		
		PieFile local = this.getLocalPieFile(remoteFile);
		return local.compareTo(remoteFile);
	}
}
