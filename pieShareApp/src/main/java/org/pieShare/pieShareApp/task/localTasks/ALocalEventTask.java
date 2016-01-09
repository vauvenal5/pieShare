/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks;

import java.io.File;
import org.pieShare.pieShareApp.model.message.api.IFilderMessageBase;
import org.pieShare.pieShareApp.model.pieFilder.PieFilder;
import org.pieShare.pieShareApp.service.fileFilterService.api.IFileFilterService;
import org.pieShare.pieShareApp.service.folderService.IFilderService;
import org.pieShare.pieShareApp.task.AMessageSendingTask;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author daniela
 */
public abstract class ALocalEventTask extends AMessageSendingTask {

    protected File file;
    protected IFileFilterService fileFilterService;
    protected IFilderService filderService;

    public ALocalEventTask() {
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setFileFilterService(IFileFilterService fileFilterService) {
        this.fileFilterService = fileFilterService;
    }

    public void setFileService(IFilderService filderService) {
        PieLogger.info(this.getClass(), "Setting FilderService!");
        this.filderService = filderService;
    }

    protected boolean syncAllowed() {
        if (this.fileFilterService.checkFile(this.file)) {
            return true;
        }
        return false;
    }

    protected <T extends PieFilder> void doWork(IFilderMessageBase<T> msg, T filder) {
        try {
            msg.setPieFilder(filder);

            this.setDefaultAdresse(msg);

            this.clusterManagementService.sendMessage(msg);
        } catch (ClusterManagmentServiceException ex) {
            PieLogger.info(this.getClass(), "Local file or folder messed up!", ex);
        }
    }

}
