/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieTask;

/**
 *
 * @author Richard
 */
public class FileCopyObserverTask implements IPieTask {

	private IFileService fileService;
	private File file;

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}
	
	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public void run() {

		FileInputStream st;

		boolean isCopying = true;

		while (isCopying) {

			try {
				Thread.sleep(2000);
				st = new FileInputStream(this.file);
				isCopying = false;
				st.close();
			} catch (FileNotFoundException ex) {

			} catch (IOException ex) {

			} catch (InterruptedException ex) {

			}
		}

		fileService.localFileChange(file);
	}
}
