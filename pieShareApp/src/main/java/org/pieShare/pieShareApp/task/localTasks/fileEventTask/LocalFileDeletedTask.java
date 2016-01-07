/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.fileEventTask;

import java.io.IOException;
import org.pieShare.pieShareApp.model.message.api.IFilderMessageBase;
import org.pieShare.pieShareApp.model.pieFilder.PieFilder;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class LocalFileDeletedTask extends ALocalFileEventTask {

    @Override
    public void run() {
        try {
            //TODO Delete Folder

            /*	PieFile pieFile = (PieFile)this.prepareWork();
             if(pieFile == null) {
             return;
             }
			
             pieFile = this.historyService.syncDeleteToHistory(pieFile);
             IFileDeletedMessage msg = this.messageFactoryService.getFileDeletedMessage();
             super.doWork(msg, pieFile);
             */
            this.isFolder = true;
            PieFilder pieFilder = this.prepareWork();
            IFilderMessageBase msg = null;

            if (pieFilder == null || this.file == null) {
                PieLogger.info(this.getClass(), "Skipping delete folder: null");
                return;
            }
            if(this.file.isDirectory() || this.isFolder) {
                msg = this.messageFactoryService.getFolderDeletedMessage();
                PieLogger.info(this.getClass(), "It's a Folder to be deleted!");
                //TODO: History Service for Folder
            } else if (this.file.isFile()) {
                msg = this.messageFactoryService.getFileDeletedMessage();
                pieFilder = this.historyService.syncDeleteToHistory((PieFile)pieFilder);
                //super.doWork(msg, pieFile);
                //return;
            } else {
                PieLogger.info(this.getClass(), "Skipping delete: unknown type (neither file nor folder)");
            }

            super.doWork(msg, pieFilder);
            
        } catch (IOException ex) {
            //todo: do something here
        }
    }

}
