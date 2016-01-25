/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.fileEventTask;

import java.io.File;
import java.io.IOException;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.api.IFileWatcherService;
import org.pieShare.pieShareApp.task.localTasks.ALocalEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public abstract class ALocalFileEventTask extends ALocalEventTask {

    protected IFileService fileService;
    protected IFileWatcherService fileWatcherService;

    public ALocalFileEventTask() {
    }

    public void setFileWatcherService(IFileWatcherService fileWatcherService) {
        this.fileWatcherService = fileWatcherService;
    }

    public void setFileService(IFileService fileService) {
        PieLogger.info(this.getClass(), "Setting FileService!");
        this.fileService = fileService;
    }

    protected PieFile prepareWork() throws IOException {        
        PieLogger.info(this.getClass(), "It's a File!");

        this.fileService.waitUntilCopyFinished(file);

        PieFile pieFile = this.fileService.getPieFile(file);

        PieFile oldPieFile = this.historyService.getPieFile(this.fileService.relativizeFilePath(file));

        if (oldPieFile != null && oldPieFile.equals(pieFile)) {
            return null;
        }

        if (this.fileWatcherService.isPieFileModifiedByUs(pieFile)) {
            this.fileWatcherService.removePieFileFromModifiedList(pieFile);
            return null;
        }
		
		if(fileFilterService.checkFile(pieFile))  {
			return null;
		}

        return pieFile;
    }

}
