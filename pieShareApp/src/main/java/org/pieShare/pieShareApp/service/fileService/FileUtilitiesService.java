/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.service.fileService.api.IFilderIterationCallback;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.folderService.IFolderService;
import org.pieShare.pieShareApp.service.fileService.api.IFileUtilitiesService;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class FileUtilitiesService implements IFileUtilitiesService {
	private IFileService fileService;
	private IFolderService folderService;

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	public void setFolderService(IFolderService folderService) {
		this.folderService = folderService;
	}
	
	@Override
	public void walkFilderTree(File parentDir, IFilderIterationCallback callback) {
		File[] files = parentDir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				callback.handleFolder(folderService.getPieFolder(file));
				walkFilderTree(file, callback);
			} else {
				try {
					callback.handleFile(fileService.getPieFile(file));
				} catch (IOException ex) {
					//ignore
				}
			}
		}
	}
}
