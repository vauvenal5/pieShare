/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.folderEventTask;

import org.pieShare.pieShareApp.model.message.folderMessages.FolderDeleteMessage;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.factoryService.MessageFactoryService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author daniela
 */
public class LocalFolderDeletedTask extends ALocalFolderEventTask {

    private MessageFactoryService msgService;
    private PieFolder pieFolder;

    @Override
    public void run() {
        pieFolder = this.prepareWork();
        
        if (pieFolder == null || this.file == null) {
            PieLogger.info(this.getClass(), "Skipping delete folder: null");
            return;
        }

        FolderDeleteMessage msg = msgService.getFolderDeletedMessage();
        PieLogger.info(this.getClass(), "It's a Folder to be deleted!");

        //TODO: add history service when ready.
        super.doWork(msg, pieFolder);
    }
}
