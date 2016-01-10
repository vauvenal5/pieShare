/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.fileEventTask;

import java.io.IOException;
import org.pieShare.pieShareApp.model.message.fileMessageBase.FileCreatedMessage;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class LocalFileCreatedTask extends ALocalFileEventTask {
    @Override
    public void run() {
        try {
            PieFile pieFile = this.prepareWork();

            if (pieFile == null || this.file == null) {
                PieLogger.info(this.getClass(), "Skipping new file: null");
                return;
            }

            FileCreatedMessage msg = this.messageFactoryService.getNewFileMessage();
            this.historyService.syncPieFileWithDb(pieFile);

            super.doWork(msg, pieFile);

        } catch (IOException ex) {
            //todo: ex handling
            PieLogger.error(this.getClass(), "Something went wrong while sending a FileDeleteMessage: {}", ex);

        }
    }
}
