/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks;

import java.io.IOException;
import java.util.List;
import org.pieShare.pieShareApp.model.message.FileListRequestMessage;
import org.pieShare.pieShareApp.model.message.api.IFileListMessage;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.historyService.IHistoryService;
import org.pieShare.pieShareApp.task.AMessageSendingEventTask;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class FileListRequestTask extends AMessageSendingEventTask<FileListRequestMessage> {

	private IHistoryService historyService;

	public void setHistoryService(IHistoryService historyService) {
		this.historyService = historyService;
	}

	@Override
	public void run() {
		List<PieFile> pieFiles;
		pieFiles = this.historyService.getPieFiles();

		//todo: use bean service instead
		IFileListMessage reply = this.messageFactoryService.getFileListMessage();
		reply.setFileList(pieFiles);
		reply.setAddress(this.msg.getAddress());

		this.setDefaultAdresse(reply);

		try {
			this.clusterManagementService.sendMessage(reply);
		} catch (ClusterManagmentServiceException ex) {
			//todo: error handling
		}
	}

}
