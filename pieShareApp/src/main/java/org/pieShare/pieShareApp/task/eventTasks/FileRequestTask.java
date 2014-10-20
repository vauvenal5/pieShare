/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks;

import java.io.File;
import java.io.IOException;
import org.pieShare.pieShareApp.model.message.FileRequestMessage;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.fileService.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.api.IFileUtilsService;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class FileRequestTask extends PieEventTaskBase<FileRequestMessage> {

	private IFileService fileService;
	private IShareService shareService;
	private IHashService hashService;
	private IRequestService requestService;
	private IPieShareAppConfiguration pieAppConfig;
	private IFileUtilsService fileUtilsService;

	public void setPieAppConfig(IPieShareAppConfiguration pieAppConfig) {
		this.pieAppConfig = pieAppConfig;
	}

	public void setFileUtilsService(IFileUtilsService fileUtilsService) {
		this.fileUtilsService = fileUtilsService;
	}

	public void setShareService(IShareService shareService) {
		this.shareService = shareService;
	}

	public void setHashService(IHashService hashService) {
		this.hashService = hashService;
	}

	public void setRequestService(IRequestService requestService) {
		this.requestService = requestService;
	}

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	@Override
	public void run() {
		File file = new File(pieAppConfig.getWorkingDirectory(), msg.getPieFile().getRelativeFilePath());

		if (!file.exists()) {
			//if the file doesn't exist on this client it could be due the fact that itself
			//is requesting it right now
			requestService.checkForActiveFileHandle(msg.getPieFile());
			return;
		}
		
		//shareService.handleActiveShare(msg.getPieFile());

		PieFile pieFile = null;

		try {
			pieFile = this.fileUtilsService.getPieFile(file);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "File error.", ex);
			return;
		}

		if (hashService.isMD5Equal(msg.getPieFile().getMd5(), pieFile.getMd5())) {
			shareService.shareFile(file);
		}
		//todo: what happens when it is the "same file" with different MD5?!
	}

}
