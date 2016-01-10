/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.folderEventTask;

import org.pieShare.pieShareApp.model.message.folderMessages.FolderCreateMessage;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author daniela
 */
public class LocalFolderCreatedTask extends ALocalFolderEventTask {

    @Override
    public void run() {
        PieFolder pieFolder = this.prepareWork();

        if (pieFolder == null || this.file == null) {
            PieLogger.info(this.getClass(), "Skipping new folder: null");
            return;
        }

        FolderCreateMessage msg = messageFactoryService.getNewFolderMessage();

        //TODO: add history service when ready.
        
        super.doWork(msg, pieFolder);
    }

}
