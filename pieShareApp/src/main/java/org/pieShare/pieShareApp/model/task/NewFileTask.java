/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.task;

import java.io.IOException;
import org.pieShare.pieShareApp.model.message.NewFileMessage;
import org.pieShare.pieShareApp.service.comparerService.api.IComparerService;
import org.pieShare.pieShareApp.service.comparerService.exceptions.FileConflictException;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

/**
 *
 * @author Svetoslav
 */
public class NewFileTask implements IPieEventTask<NewFileMessage> {

	private NewFileMessage msg;
        private IComparerService comparerService;

        public void setComparerService(IComparerService comparerService) {
            this.comparerService = comparerService;
        }

	@Override
	public void setMsg(NewFileMessage msg) {
		this.msg = msg;
	}

	@Override
	public void run() {
            try {
                    comparerService.comparePieFile(msg.getPieFile());
            } catch (IOException ex) {
                    //TODO: Handle
            } catch (FileConflictException ex) {
                    //TODO: Handle
            }
	}

}
