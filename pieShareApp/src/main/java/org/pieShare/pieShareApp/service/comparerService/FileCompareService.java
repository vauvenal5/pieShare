/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.comparerService;

import java.io.IOException;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.comparerService.api.ILocalFileCompareService;
import org.pieShare.pieShareApp.service.comparerService.exceptions.FileConflictException;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class FileCompareService extends FileHistoryCompareService implements ILocalFileCompareService {
	private IFileService fileService;
	
	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}
	
	@Override
	public int compareWithLocalPieFile(PieFile pieFile) throws IOException, FileConflictException {
		try {
			return super.compareWithLocalPieFile(pieFile);
		}
		catch(IOException | FileConflictException ex)
		{
			//ignore this and check the real file!
		}
		
		PieFile localPieFile = this.fileService.getPieFile(pieFile.getRelativeFilePath());
		
		if(localPieFile == null) {
			PieLogger.debug(this.getClass(), "{} does not exist. Request this file.", pieFile.getRelativeFilePath());
			return 1;
		}

		return this.comparePieFiles(pieFile, localPieFile);
	}
}
