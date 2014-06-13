/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileListenerService;

import java.io.File;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.service.fileService.api.IFileObserver;
import org.pieShare.pieShareApp.service.fileService.task.FileChangedTask;
import org.pieShare.pieShareApp.service.fileService.task.FileCreatedTask;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieTask;

/**
 *
 * @author richy
 */
public class ApacheDefaultFileListener implements FileListener {

	private IFileObserver fileObserver;
	private IExecutorService executerService;
	private IBeanService beanService;

	public void setFileObserver(IFileObserver fileObserver) {
		this.fileObserver = fileObserver;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setExecutorService(IExecutorService executerService) {
		this.executerService = executerService;
	}

	@Override
	public void fileCreated(FileChangeEvent fce) throws Exception {
		String filePath = fce.getFile().getURL().getFile();
		FileCreatedTask task = beanService.getBean(PieShareAppBeanNames.getFileCreatedTaskName());
		File file = new File(filePath);
		task.setCreatedFile(file);
		startObservation(file, task);
	}

	@Override
	public void fileDeleted(FileChangeEvent fce) throws Exception {
		/*String filePath = fce.getFile().getURL().getFile();
		 PieFile pieFile = new PieFile();
		 pieFile.Init(new File(filePath));
	
		 shareService.shareFile(pieFile);
		 fileMerger.fileDeleted(new File(filePath));
		 //startObservation(new File(filePath), FileChangedTypes.FILE_DELETED);*/
	}

	@Override
	public void fileChanged(FileChangeEvent fce) throws Exception {
		String filePath = fce.getFile().getURL().getFile();
		FileChangedTask task = beanService.getBean(PieShareAppBeanNames.getFileChangedTaskName());
		File file = new File(filePath);
		task.setCreatedFile(file);
		startObservation(file, task);
	}

	private void startObservation(File file, IPieTask task) {
		fileObserver.setData(file);
		fileObserver.setTask(task);
		executerService.execute(fileObserver);
	}

}
