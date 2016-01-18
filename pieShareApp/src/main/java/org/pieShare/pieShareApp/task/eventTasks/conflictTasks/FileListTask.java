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
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class FileListTask extends ARequestTask<IFileListMessage> {

	private IFileService fileService;
	private IFolderService folderService;

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	public void setFolderService(IFolderService folderService) {
		this.folderService = folderService;
	}

	@Override
	public void run() {

		//todo-after-ase: this is not entirely correct and only a temporary solution
			//we need to perform all relevant local checks to ensure that a
			//folder/file is allowed to be delete otherwise race conditions are possible
			//+ a folder needs a last modified so that when we know which event has priority
		for (PieFile file : this.msg.getFileList()) {
			if (file.isDeleted()) {
				//todo: can not simply delete file!
					//we need to ask for the history so that we can determin
					//if we have a conflict or not
				fileService.deleteRecursive(file);
			} else {
				//in future consider starting an own task for requesting files because we don't 
				//want to block by user resolution all autoresolvable requests
				this.doWork(file);
			}
		}

		for (PieFolder folder : this.msg.getFolderList()) {
			try {
				if (folder.isDeleted()) {
					this.folderService.deleteFolder(folder);
				} else {
					if(!this.folderService.getAbsolutePath(folder).exists()) {
						this.folderService.createFolder(folder);
					}
				}
			} catch (FolderServiceException ex) {
				PieLogger.warn(this.getClass(), "An unexpected exception was thrown!", ex);
			}
		}
	}
}
