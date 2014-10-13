/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.task;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.message.FileListMessage;
import org.pieShare.pieShareApp.model.message.FileListRequestMessage;
import org.pieShare.pieShareApp.service.fileService.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

/**
 *
 * @author Svetoslav
 */
public class FileListRequestTask implements IPieEventTask<FileListRequestMessage> {

	private FileListRequestMessage msg;
	private IFileService fileService;
	private IClusterManagementService clusterManagementService;
	
	@Override
	public void setMsg(FileListRequestMessage msg) {
		this.msg = msg;
	}

	@Override
	public void run() {
		List<PieFile> pieFiles;
		try {
			pieFiles = this.fileService.getAllFilesList();
		
			//todo: use bean service instead
			FileListMessage reply = new FileListMessage();
                        reply.setFileList(pieFiles);
			reply.setAddress(this.msg.getAddress());

			try {
				this.clusterManagementService.sendMessage(reply);
			}
			catch(ClusterManagmentServiceException ex) {
				//todo: error handling
			}
		
		} catch (IOException ex) {
			//todo: error handling
		}
	}
	
}
