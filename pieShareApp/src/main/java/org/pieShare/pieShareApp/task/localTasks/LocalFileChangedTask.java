/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks;

import java.io.File;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieTask;

/**
 *
 * @author Richard
 */
//todo: deprecated?
public class LocalFileChangedTask implements IPieTask {

	private IFileService fileService;
	private String filePath;
	
	public void setFileService(IFileService service) {
		this.fileService = service;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public void run() {
		File file = new File(this.filePath);
		this.fileService.waitUntilCopyFinished(file);
	}

}
