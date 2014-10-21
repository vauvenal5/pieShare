/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileListenerService;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.service.fileService.api.IFileUtilsService;
import org.pieShare.pieShareApp.task.localTasks.LocalFileChangedTask;
import org.pieShare.pieShareApp.task.localTasks.LocalFileCreatedTask;
import org.pieShare.pieShareApp.task.localTasks.LocalFileDeletedTask;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
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
	private IFileUtilsService utilsService;
	private IClusterManagementService clusterManagementService;

	/*public void setFileObserver(IFileObserver fileObserver) {
		this.fileObserver = fileObserver;
	}*/

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setUtilsService(IFileUtilsService utilsService) {
		this.utilsService = utilsService;
	}

	public void setClusterManagementService(IClusterManagementService clusterManagementService) {
		this.clusterManagementService = clusterManagementService;
	}

	public void setExecutorService(IExecutorService executerService) {
		this.executerService = executerService;
	}

	@Override
	public void fileCreated(FileChangeEvent fce) throws Exception {
		String filePath = fce.getFile().getURL().getFile();
		PieLogger.info(this.getClass(), "File created: {}", filePath);
		LocalFileCreatedTask task = beanService.getBean(PieShareAppBeanNames.getLocalFileCreatedTask());
		task.setFilePath(filePath);
		this.executerService.execute(task);
	}

	@Override
	public void fileDeleted(FileChangeEvent fce) throws Exception {
		//todo: does the file delete comand also has to wait like file created until the delete has finished?
		String filePath = fce.getFile().getURL().getFile();
		PieLogger.info(this.getClass(), "File deleted: {}", filePath);
		LocalFileDeletedTask task = beanService.getBean(PieShareAppBeanNames.getLocalFileDeletedTask());
		task.setFilePath(filePath);
		this.executerService.execute(task);
	}

	@Override
	public void fileChanged(FileChangeEvent fce) throws Exception {
		String filePath = fce.getFile().getURL().getFile();
		PieLogger.info(this.getClass(), "File changed: {}", filePath);
		LocalFileChangedTask task = beanService.getBean(PieShareAppBeanNames.getFileChangedTaskName());
		task.setFilePath(filePath);
		this.executerService.execute(task);
	}
}
