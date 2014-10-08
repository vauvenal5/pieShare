/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.task;

import org.pieShare.pieShareApp.model.message.FileListMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

/**
 *
 * @author Svetoslav
 */
public class FileListTask implements IPieEventTask<FileListMessage>  {

	private FileListMessage msg;
	private IFileService fileService;
	
	@Override
	public void setMsg(FileListMessage msg) {
		this.msg = msg;
	}

	@Override
	public void run() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
