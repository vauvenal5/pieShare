/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.eventTasks;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.api.IFileRequestMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareConfiguration;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.fileEncryptionService.IFileEncryptionService;
import org.pieShare.pieShareApp.service.requestService.api.IRequestService;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class FileRequestTask extends PieEventTaskBase<IFileRequestMessage> {

	private IFileService fileService;
	private IShareService shareService;
	private IHashService hashService;
	private IRequestService requestService;
	private IBeanService beanService;
	private IFileEncryptionService fileEncryptionService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
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

		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		IPieShareConfiguration configuration = user.getPieShareConfiguration();

		//todo: fileExists maybe belongs into fileservice
		File file = new File(configuration.getWorkingDir(), this.msg.getPieFile().getRelativeFilePath());
		
		if (!file.exists()) {
			//if the file doesn't exist on this client it could be due the fact that itself
			//is requesting it right now
			if(requestService.isRequested(msg.getPieFile())) {
				shareService.handleRemoteRequestForActiveShare(msg.getPieFile());
			}
			return;
		}

		//shareService.handleActiveShare(msg.getPieFile());
		PieFile pieFile = null;

		try {
			pieFile = this.fileService.getPieFile(file);
		}
		catch (IOException ex) {
			PieLogger.error(this.getClass(), "File error.", ex);
			return;
		}

		if (hashService.isMD5Equal(msg.getPieFile().getMd5(), pieFile.getMd5())) {
			this.shareService.shareFile(pieFile);
			//todo: what happens when it is the "same file" with different MD5?!
		}
	}

}
