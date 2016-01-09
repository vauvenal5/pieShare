/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.folderEventTask;

import javax.inject.Provider;
import org.pieShare.pieShareApp.model.message.folderMessages.FolderCreateMessage;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.ALocalFileEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author daniela
 */
public class LocalFolderCreatedTask extends ALocalFileEventTask{
    PieFolder pieFolder;
    private Provider<FolderCreateMessage> msgProvider;
    
    @Override
    public void run() {
        if(pieFolder == null) {
            PieLogger.info(this.getClass(), "No Folder set, pieFolder:", pieFolder);
            return;
        }
        //add prepare work
                    
        //this.clusterManagementService.sendMessage(msgProvider.get());
        FolderCreateMessage msg = msgProvider.get();
            
        //TODO: add history service when ready.
        
        super.doWork(msg, pieFolder);
    }

    //should be called by OS Listener
    public void setPieFolder(PieFolder pieFolder) {
        this.pieFolder = pieFolder;
    }
    
    
}
