/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.service.fileService.api.IFileObserver;
import org.pieShare.pieShareApp.service.fileService.task.FileCreatedTask;
import org.pieShare.pieShareApp.service.shareService.IShareService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieTask;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;

/**
 *
 * @author Richard
 */
public class FileObserver implements IFileObserver {

	private IExecutorService executorService;
	private File file;
	private IBeanService beanService;
	private final long TIME_OUT_SEC = 60 * 60;
	public IPieTask task;

	@Override
	public void setTask(IPieTask task) {
		this.task = task;
	}

	public void setExecutorService(IExecutorService executorService) {
		this.executorService = executorService;
	}

	@Override
	public void setData(File file) {
		this.file = file;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	@Override
	public void run() {

		FileInputStream st;

		boolean isCopying = true;

		while (isCopying) {

			try {
				Thread.sleep(2000);
				st = new FileInputStream(file);
				isCopying = false;
				st.close();
			} catch (FileNotFoundException ex) {

			} catch (IOException ex) {

			} catch (InterruptedException ex) {

			}

		}

		executorService.execute(task);
	}

}
