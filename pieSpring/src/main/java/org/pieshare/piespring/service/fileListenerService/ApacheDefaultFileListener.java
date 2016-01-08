/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.piespring.service.fileListenerService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Provider;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.pieShare.pieShareApp.model.LocalFileEvent;
import org.pieShare.pieShareApp.model.LocalFileEventType;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.eventFolding.IEventFoldingService;
import org.pieshare.piespring.service.fileListenerService.api.IFileListenerService;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileChangedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileCreatedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileDeletedTask;
import org.pieshare.piespring.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author richy
 */
public class ApacheDefaultFileListener implements IFileListenerService {

//	private IExecutorService executerService;
//	private IBeanService beanService;
	private IEventFoldingService eventFoldingService;
	private Provider<LocalFileEvent> localFileEventProvider;

//	public void setBeanService(IBeanService beanService) {
//		this.beanService = beanService;
//	}
//
//	public void setExecutorService(IExecutorService executerService) {
//		this.executerService = executerService;
//	}

	public void setEventFoldingService(IEventFoldingService eventFoldingService) {
		this.eventFoldingService = eventFoldingService;
	}

	public void setLocalFileEventProvider(Provider<LocalFileEvent> localFileEventProvider) {
		this.localFileEventProvider = localFileEventProvider;
	}
	
	private File convertFileObject(FileObject object) {
		return new File(object.getName().getPath());
	}
	
	private void sendEvent(LocalFileEventType type, FileChangeEvent fileChangeEvent) {
		PieLogger.info(this.getClass(), "File event {} for: {}", type.toString(), fileChangeEvent.getFile().getName().getPath());
		LocalFileEvent event = this.localFileEventProvider.get();
		event.setType(type);
		event.setFile(this.convertFileObject(fileChangeEvent.getFile()));
		this.eventFoldingService.handleLocalEvent(event);
	}

	@Override
	public void fileCreated(FileChangeEvent fce) throws Exception {
		this.sendEvent(LocalFileEventType.CREATED, fce);
//		PieLogger.info(this.getClass(), "File created: {}", fce.getFile().getName().getPath());
//		LocalFileCreatedTask task = beanService.getBean(PieShareAppBeanNames.getLocalFileCreatedTask());
//		task.setFile(this.convertFileObject(fce.getFile()));
//		this.executerService.execute(task);
	}

	@Override
	public void fileDeleted(FileChangeEvent fce) throws Exception {
		this.sendEvent(LocalFileEventType.DELETED, fce);
		//todo: does the file delete comand also has to wait like file created until the delete has finished?
//		PieLogger.info(this.getClass(), "File deleted: {}", fce.getFile().getName().getPath());
//		LocalFileDeletedTask task = beanService.getBean(PieShareAppBeanNames.getLocalFileDeletedTask());
//		task.setFile(this.convertFileObject(fce.getFile()));
//		this.executerService.execute(task);
	}

	@Override
	public void fileChanged(FileChangeEvent fce) throws Exception {
		this.sendEvent(LocalFileEventType.MODIFIED, fce);
//		PieLogger.info(this.getClass(), "File changed: {}", fce.getFile().getName().getPath());
//		LocalFileChangedTask task = beanService.getBean(PieShareAppBeanNames.getFileChangedTaskName());
//		task.setFile(this.convertFileObject(fce.getFile()));
//		this.executerService.execute(task);
	}
}
