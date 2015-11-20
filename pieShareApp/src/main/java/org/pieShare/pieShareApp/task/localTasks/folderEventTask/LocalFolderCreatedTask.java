/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.folderEventTask;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Provider;
import org.pieShare.pieShareApp.model.message.folderMessages.FolderCreateMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFolder;
import org.pieShare.pieShareApp.task.AMessageSendingTask;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.event.IPieEvent;

/**
 *
 * @author daniela
 */
public class LocalFolderCreatedTask extends AMessageSendingTask{
    PieFolder pieFolder;
    private Provider<FolderCreateMessage> msgProvider;
    
    @Override
    public void run() {
        try {
            this.clusterManagementService.sendMessage(msgProvider.get());
            
        } catch (ClusterManagmentServiceException ex) {
            //TODO use right logger
            Logger.getLogger(LocalFolderCreatedTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //should be called by OS Listener
    public void setPieFolder(PieFolder pieFolder) {
        this.pieFolder = pieFolder;
    }
    
    
}
