/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.piespring.service.fileListenerService;

import javax.inject.Provider;
import org.apache.commons.vfs2.FileChangeEvent;
import org.pieShare.pieShareApp.model.LocalFileEventType;
import org.pieShare.pieShareApp.task.localTasks.EventFoldingServiceTask;
import org.pieshare.piespring.service.fileListenerService.api.IFileListenerService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author richy
 */
public class ApacheDefaultFileListener implements IFileListenerService {

	private IExecutorService executerService;
//	private IBeanService beanService;
        private Provider<EventFoldingServiceTask> eventFoldignServiceTask;

//	public void setBeanService(IBeanService beanService) {
//		this.beanService = beanService;
//	}
//
	public void setExecutorService(IExecutorService executerService) {
		this.executerService = executerService;
	}

        public void setEventFoldingServiceTask(Provider<EventFoldingServiceTask> eventFoldignServiceTask) {
		this.eventFoldignServiceTask = eventFoldignServiceTask;
	}


	@Override
	public void fileCreated(FileChangeEvent fce) throws Exception {
		LocalFileEventType type = LocalFileEventType.CREATED;
                
                EventFoldingServiceTask task = eventFoldignServiceTask.get();
                task.init(type, fce);
                executerService.execute(task);
//		PieLogger.info(this.getClass(), "File created: {}", fce.getFile().getName().getPath());
//		LocalFileCreatedTask task = beanService.getBean(PieShareAppBeanNames.getLocalFileCreatedTask());
//		task.setFile(this.convertFileObject(fce.getFile()));
//		this.executerService.execute(task);
	}

	@Override
	public void fileDeleted(FileChangeEvent fce) throws Exception {
		LocalFileEventType type = LocalFileEventType.DELETED;
                
                EventFoldingServiceTask task = eventFoldignServiceTask.get();
                task.init(type, fce);
                executerService.execute(task);
		//todo: does the file delete comand also has to wait like file created until the delete has finished?
//		PieLogger.info(this.getClass(), "File deleted: {}", fce.getFile().getName().getPath());
//		LocalFileDeletedTask task = beanService.getBean(PieShareAppBeanNames.getLocalFileDeletedTask());
//		task.setFile(this.convertFileObject(fce.getFile()));
//		this.executerService.execute(task);
	}

	@Override
	public void fileChanged(FileChangeEvent fce) throws Exception {
		LocalFileEventType type = LocalFileEventType.MODIFIED;
		
                EventFoldingServiceTask task = eventFoldignServiceTask.get();
                task.init(type, fce);
                executerService.execute(task);
//		LocalFileChangedTask task = beanService.getBean(PieShareAppBeanNames.getFileChangedTaskName());
//		task.setFile(this.convertFileObject(fce.getFile()));
//		this.executerService.execute(task);
	}
}
