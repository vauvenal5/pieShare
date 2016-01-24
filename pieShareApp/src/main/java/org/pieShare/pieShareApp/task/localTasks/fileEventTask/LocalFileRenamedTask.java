/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks.fileEventTask;

import java.io.IOException;
import org.pieShare.pieShareApp.model.message.fileHistoryMessage.FileMovedMessage;
import org.pieShare.pieShareApp.model.message.fileHistoryMessage.FileRenamedMessage;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Chris
 */
public class LocalFileRenamedTask extends ALocalFileEventTask {

    @Override
    public void run() {
        try {

            PieFile pieFile = this.prepareWork();

            if (pieFile == null || this.file == null) {
                PieLogger.info(this.getClass(), "Skipping rename file: null");
                return;
            }

            FileRenamedMessage msg = this.messageFactoryService.getFileRenamedMessage();
            PieFile oldFile = this.prepareOldFile();
            msg.setPreviousFile(oldFile);
            this.historyService.syncPieFile(pieFile);

            super.doWork(msg, pieFile);

        } catch (IOException ex) {
            //todo: do something here
            PieLogger.error(this.getClass(), "Something went wrong while sending a FileRenamedMessage: {}", ex);

        }
    }
    
}
