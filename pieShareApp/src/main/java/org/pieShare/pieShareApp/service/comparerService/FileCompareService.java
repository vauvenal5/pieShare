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
public class FileCompareService extends ALocalFileCompareService implements ILocalFileCompareService {
	private IFileService fileService;
	
	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	@Override
	protected PieFile getLocalPieFile(PieFile remoteFile) throws NullPointerException, IOException {
		return this.fileService.getPieFile(remoteFile.getRelativeFilePath());
	}
}
