/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task;

import java.io.File;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieTask;

/**
 *
 * @author Richard
 */
//todo: deprecated?
public class FileChangedTask implements IPieTask {

	private File file;
	private IFileService fileService;

	public void setCreatedFile(File file) {
		this.file = file;
	}

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	@Override
	public void run() {
		fileService.localFileChange(file);
	}

}
