/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.task;

import org.pieShare.pieShareApp.model.message.NewFileMessage;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

/**
 *
 * @author Svetoslav
 */
public class NewFileTask implements IPieEventTask<NewFileMessage> {

	private NewFileMessage msg;
	private IFileService fileService;

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	@Override
	public void setMsg(NewFileMessage msg) {
		this.msg = msg;
	}

	@Override
	public void run() {
		this.fileService.remoteFileChanged(msg);
	}

}
