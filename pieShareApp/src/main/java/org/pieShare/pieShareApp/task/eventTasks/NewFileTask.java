/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks;

import java.io.IOException;
import org.pieShare.pieShareApp.model.message.NewFileMessage;
import org.pieShare.pieShareApp.service.comparerService.api.IComparerService;
import org.pieShare.pieShareApp.service.comparerService.exceptions.FileConflictException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class NewFileTask extends PieEventTaskBase<NewFileMessage> {

        private IComparerService comparerService;

        public void setComparerService(IComparerService comparerService) {
            this.comparerService = comparerService;
        }

	@Override
	public void run() {
            try {
                    comparerService.comparePieFile(msg.getPieFile());
            } catch (IOException ex) {
                    PieLogger.error(this.getClass(), "New File Task error.", ex);
            } catch (FileConflictException ex) {
                    PieLogger.error(this.getClass(), "New File Task error.", ex);
            }
	}

}
