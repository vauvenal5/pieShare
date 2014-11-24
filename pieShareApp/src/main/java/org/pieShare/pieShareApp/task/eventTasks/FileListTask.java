/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task.eventTasks;

import java.util.concurrent.ExecutorService;
import org.pieShare.pieShareApp.model.message.FileListMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.task.localTasks.ComparePieFileTask;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.pieShare.pieTools.pieUtilities.task.PieEventTaskBase;

/**
 *
 * @author Svetoslav
 */
public class FileListTask extends PieEventTaskBase<FileListMessage>  {
	
	private BeanService beanService;
	private PieExecutorService executorService;

	public void setBeanService(BeanService beanService) {
		this.beanService = beanService;
	}

	public void setExecutorService(PieExecutorService executorService) {
		this.executorService = executorService;
	}

	@Override
	public void run() {
		for(PieFile file: this.msg.getFileList()) {
			ComparePieFileTask task = this.beanService.getBean(ComparePieFileTask.class);
			task.setPieFile(file);
			this.executorService.execute(task);
		}
	}
	
}
