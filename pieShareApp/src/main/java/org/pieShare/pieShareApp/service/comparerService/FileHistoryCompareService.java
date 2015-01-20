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

/**
 *
 * @author Svetoslav
 */
public class FileHistoryCompareService extends ComparerService implements ILocalFileCompareService {
	private IFileService historyService;
	
	@Override
	public int compareWithLocalPieFile(PieFile pieFile) throws IOException, FileConflictException {
		try {
			PieFile file = this.historyService.getWorkingPieFile(pieFile);
			return this.comparePieFiles(pieFile, file);
		} catch (IOException ex) {
			//ignore if no history exists maybe it is still not writen
		}
		
		return this.compareWithLocalPieFile(pieFile);
	}
}
