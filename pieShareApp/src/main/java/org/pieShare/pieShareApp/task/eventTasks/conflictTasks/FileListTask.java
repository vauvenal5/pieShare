/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks.conflictTasks;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Provider;
import org.pieShare.pieShareApp.model.message.api.IFileListMessage;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.folderService.FolderServiceException;
import org.pieShare.pieShareApp.service.folderService.IFolderService;
import org.pieShare.pieShareApp.service.historyService.IHistoryService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class FileListTask extends ARequestTask<IFileListMessage> {

	private IFileService fileService;
	private IFolderService folderService;
//	private IHistoryService historyService;

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	public void setFolderService(IFolderService folderService) {
		this.folderService = folderService;
	}

	@Override
	public void run() {
		//todo-after-ase: this is just a quickfix to make the list sync work at least for delete and create
		//we will need to implement a full history to ensure proper resync after being offline
		for (PieFile file : this.msg.getFileList()) {
			if (!this.isConflictedOrNotNeeded(file)) {
				if (file.isDeleted()) {
					fileService.deleteRecursive(file);
				} else {
					//in future consider starting an own task for requesting files because we don't 
					//want to block by user resolution all autoresolvable requests
					this.requestService.requestFile(file);
				}
			}
		}

		for (PieFolder folder : this.msg.getFolderList()) {
			try {
				if (!this.comparerService.isConflictedOrNotNeeded(folder)) {
					if (folder.isDeleted()) {
						this.folderService.deleteFolder(folder);
					} else if (!this.folderService.getAbsolutePath(folder).exists()) {
						this.folderService.createFolder(folder);
					}
				}
			} catch (FolderServiceException ex) {
				PieLogger.warn(this.getClass(), "An unexpected exception was thrown!", ex);
			}
		}
	}

	//todo-after-ase: this is also part of the quick fix! consider this two options:
	//1. extend compare service to also have functionality for folders or
	//2. remove compare service entirely and add the logic where needed in place like here
	//this would be possible due to the fact that the compare service only covers the retrieval of the
	//local or history object and performs otherwise the same as
//	private boolean conflictedOrNotNeeded(PieFolder folder) {
//		PieFolder localFolder = this.historyService.getPieFolder(folder.getRelativePath());
//
//		if (localFolder == null) {
//			return false;
//		}
//
//		if (localFolder.compareTo(folder) == -1) {
//			return false;
//		}
//
//		return true;
//	}
}
