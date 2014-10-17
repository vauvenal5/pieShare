/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileListenerService;

import java.io.File;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.task.FileCopyObserverTask;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author richy
 */
public class ApacheDefaultFileListener implements FileListener {

	//private IFileObserver fileObserver;
	private IExecutorService executerService;
	private IBeanService beanService;

	/*public void setFileObserver(IFileObserver fileObserver) {
		this.fileObserver = fileObserver;
	}*/

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setExecutorService(IExecutorService executerService) {
		this.executerService = executerService;
	}

	@Override
	public void fileCreated(FileChangeEvent fce) throws Exception {
		String filePath = fce.getFile().getURL().getFile();
		FileCopyObserverTask observerTask = beanService.getBean(PieShareAppBeanNames.getFileCopyObserverTask());
		observerTask.setFile(new File(filePath));
		executerService.execute(observerTask);
	}

	@Override
	public void fileDeleted(FileChangeEvent fce) throws Exception {
		String filePath = fce.getFile().getURL().getFile();
		PieLogger.info(this.getClass(), "File deleted: {}", filePath);
		//todo: for the time being we will just delete without checks
		//later somekinde of persistency and check has to be added
		
	}

	@Override
	public void fileChanged(FileChangeEvent fce) throws Exception {
		/*String filePath = fce.getFile().getURL().getFile();
		FileChangedTask task = beanService.getBean(PieShareAppBeanNames.getFileChangedTaskName());
		File file = new File(filePath);
		task.setCreatedFile(file);
		startObservation(file, task);*/
	}
}
