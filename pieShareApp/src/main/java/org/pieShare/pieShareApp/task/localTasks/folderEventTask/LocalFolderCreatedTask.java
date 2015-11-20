/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.folderEventTask;

import org.pieShare.pieShareApp.model.message.folderMessages.FolderCreateMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFolder;
import org.pieShare.pieShareApp.task.AMessageSendingTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.event.IPieEvent;

/**
 *
 * @author daniela
 */
public class LocalFolderCreatedTask extends AMessageSendingTask{
    PieFolder pieFolder;
    //private Provider<FolderCreateMessage> msgProvider;
    
    @Override
    public void run() {
        //FolderCreateMessage msg = ;
        //TODO send Message
    }

    //should be called by OSListener
    public void setPieFolder(PieFolder pieFolder) {
        this.pieFolder = pieFolder;
    }
    
    
}
