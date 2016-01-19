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
import org.pieShare.pieShareApp.service.folderService.IFolderService;
import org.pieShare.pieShareApp.service.comparerService.api.ICompareService;

/**
 *
 * @author Svetoslav
 */
public class LocalCompareService extends ACompareService implements ICompareService {
	private IFileService fileService;
	private IFolderService folderService;
	
	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	public void setFolderService(IFolderService folderService) {
		this.folderService = folderService;
	}

	@Override
	protected PieFile getPieFile(PieFile remoteFile) {
		try {
			return this.fileService.getPieFile(remoteFile.getRelativePath());
		} catch (IOException ex) {
			return null;
		}
	}

	@Override
	protected PieFolder getPieFolder(PieFolder folder) {
		return this.folderService.getPieFolder(folder.getRelativePath());
	}	
}
