/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.comparerService;

import java.io.IOException;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.comparerService.api.ILocalFileCompareService;
import org.pieShare.pieShareApp.service.comparerService.exceptions.FileConflictException;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.historyService.IHistoryService;

/**
 *
 * @author Svetoslav
 */
public class FileHistoryCompareService extends ALocalFileCompareService implements ILocalFileCompareService {
	private IHistoryService historyService;

	public void setHistoryService(IHistoryService historyService) {
		this.historyService = historyService;
	}

	@Override
	protected PieFile getPieFile(PieFile remoteFile) {
		return this.historyService.getPieFile(remoteFile.getRelativePath());
	}
}
