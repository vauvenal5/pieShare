/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks.folderTasks;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.message.folderMessages.FolderCreateMessage;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.folderService.FolderServiceException;
import org.pieShare.pieShareApp.service.folderService.IFolderService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.event.IPieEvent;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 * remote folder created
 * @author daniela
 */
public class FolderCreateTask extends PieEventTaskBase<FolderCreateMessage> {
    private IFolderService folderService;
    
    public void setFolderService (IFolderService folderService) {
        this.folderService = folderService;
    }
        
    @Override
    public void run() {
        try {
            folderService.createFolder(this.msg.getPieFolder());
            
        } catch (FolderServiceException ex) {
            // todo Logger.getLogger(FolderCreateTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
