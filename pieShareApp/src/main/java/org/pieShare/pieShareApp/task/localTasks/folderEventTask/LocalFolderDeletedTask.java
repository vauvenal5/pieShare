/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.folderEventTask;

import org.pieShare.pieShareApp.model.message.folderMessages.FolderDeleteMessage;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author daniela
 */
public class LocalFolderDeletedTask extends ALocalFolderEventTask {
    @Override
    public void run() {
        PieFolder pieFolder = this.prepareWork();
        
        if (pieFolder == null || this.file == null) {
            PieLogger.info(this.getClass(), "Skipping delete folder: null");
            return;
        }		

        FolderDeleteMessage msg = this.messageFactoryService.getFolderDeletedMessage();
        PieLogger.info(this.getClass(), "It's a Folder to be deleted!");

        super.doWork(msg, pieFolder);
    }
}
