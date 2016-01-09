/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.folderEventTask;

import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.folderService.IFolderService;
import org.pieShare.pieShareApp.task.localTasks.ALocalEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author daniela
 */
public abstract class ALocalFolderEventTask extends ALocalEventTask {

    protected IFolderService folderService;

    public ALocalFolderEventTask() {
    }

    public void setFolderService(IFolderService folderService) {
        PieLogger.info(this.getClass(), "Setting FolderService!");
        this.folderService = folderService;
    }

    protected PieFolder prepareWork() {
        if (!syncAllowed()) {
            return null;
        }

        if (this.file.isFile()) {
            PieLogger.error(this.getClass(), "It's a file! Why is it here? - Should be FileEventTask");
            return null;
        }

        PieLogger.info(this.getClass(), "It's a Folder!");

        PieFolder pieFolder = new PieFolder();
        pieFolder.setName(this.file.getName());
        pieFolder.setRelativePath(filderService.relativizeFilePath(this.file));
        return pieFolder;
    }

}
