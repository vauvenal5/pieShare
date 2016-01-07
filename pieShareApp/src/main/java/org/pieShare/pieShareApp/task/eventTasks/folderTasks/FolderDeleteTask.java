/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks.folderTasks;

import org.pieShare.pieShareApp.model.message.folderMessages.FolderDeleteMessage;
import org.pieShare.pieShareApp.service.folderService.FolderServiceException;
import org.pieShare.pieShareApp.service.folderService.IFolderService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 * remote folder deleted - delete local too
 * @author daniela
 */
public class FolderDeleteTask extends PieEventTaskBase<FolderDeleteMessage> {
    private IFolderService folderService;

        public void setFolderService (IFolderService folderService) {
        this.folderService = folderService;
    }
    
    @Override
    public void run() {
        try {
            folderService.deleteFolder(this.msg.getPieFilder());
        } catch (FolderServiceException ex) {
            PieLogger.debug(this.getClass(), "Folder couldn't be deleted from task. {}" + ex);
        }
    }
    
}
