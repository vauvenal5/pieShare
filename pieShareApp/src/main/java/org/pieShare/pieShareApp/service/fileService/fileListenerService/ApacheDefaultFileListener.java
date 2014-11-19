/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService.fileListenerService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.fileService.fileListenerService.api.IFileListenerService;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileChangedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileCreatedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileDeletedTask;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author richy
 */
public class ApacheDefaultFileListener implements IFileListenerService {

	private IExecutorService executerService;
	private IBeanService beanService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setExecutorService(IExecutorService executerService) {
		this.executerService = executerService;
	}
	
	private File convertFileObject(FileObject object) {
		return new File(object.getName().getPath());
	}

	@Override
	public void fileCreated(FileChangeEvent fce) throws Exception {
		PieLogger.info(this.getClass(), "File created: {}", fce.getFile().getName().getPath());
		LocalFileCreatedTask task = beanService.getBean(PieShareAppBeanNames.getLocalFileCreatedTask());
		task.setFile(this.convertFileObject(fce.getFile()));
		this.executerService.execute(task);
	}

	@Override
	public void fileDeleted(FileChangeEvent fce) throws Exception {
		//todo: does the file delete comand also has to wait like file created until the delete has finished?
		PieLogger.info(this.getClass(), "File deleted: {}", fce.getFile().getName().getPath());
		LocalFileDeletedTask task = beanService.getBean(PieShareAppBeanNames.getLocalFileDeletedTask());
		task.setFile(this.convertFileObject(fce.getFile()));
		this.executerService.execute(task);
	}

	@Override
	public void fileChanged(FileChangeEvent fce) throws Exception {
		PieLogger.info(this.getClass(), "File changed: {}", fce.getFile().getName().getPath());
		LocalFileChangedTask task = beanService.getBean(PieShareAppBeanNames.getFileChangedTaskName());
		task.setFile(this.convertFileObject(fce.getFile()));
		this.executerService.execute(task);
	}
}
