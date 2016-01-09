/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import javax.inject.Provider;
import org.pieShare.pieShareApp.model.LocalFileEvent;
import org.pieShare.pieShareApp.service.eventFolding.EventFoldingService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.ALocalFileEventTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileChangedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileCreatedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileDeletedTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class EventFoldingTimerTask extends TimerTask {
	
	private IExecutorService executorService;
	private EventFoldingService eventFoldingService;
	
	private Provider<LocalFileChangedTask> localFileChangedProvider;
	private Provider<LocalFileCreatedTask> localFileCreatedProvider;
	private Provider<LocalFileDeletedTask> localFileDeletedProvider;

	@Override
	public void run() {
		Map<String, LocalFileEvent> localEvents = this.eventFoldingService.getLocalEvents();
		
		synchronized (localEvents) {
			if (localEvents.isEmpty()) {
				return;
			}

			long currentTime = new Date().getTime();

			Iterator<Map.Entry<String, LocalFileEvent>> iterator
					= localEvents.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<String, LocalFileEvent> entry = iterator.next();

				if ((currentTime - entry.getValue().getTimestamp()) > 2000) {
					ALocalFileEventTask task = null;
					switch (entry.getValue().getType()) {
						case CREATED:
							task = localFileCreatedProvider.get();
							break;
						case DELETED:
							task = localFileDeletedProvider.get();
							break;
						default:
							task = localFileChangedProvider.get();
							break;
					}

					task.setFile(entry.getValue().getFile());
					executorService.execute(task);
					iterator.remove();
				}
			}
			
			if (!localEvents.isEmpty()) {
				this.eventFoldingService.reschedule();
			}
		}
	}

	public IExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(IExecutorService executorService) {
		this.executorService = executorService;
	}

	public EventFoldingService getEventFoldingService() {
		return eventFoldingService;
	}

	public void setEventFoldingService(EventFoldingService eventFoldingService) {
		this.eventFoldingService = eventFoldingService;
	}

	public Provider<LocalFileChangedTask> getLocalFileChangedProvider() {
		return localFileChangedProvider;
	}

	public void setLocalFileChangedProvider(Provider<LocalFileChangedTask> localFileChangedProvider) {
		this.localFileChangedProvider = localFileChangedProvider;
	}

	public Provider<LocalFileCreatedTask> getLocalFileCreatedProvider() {
		return localFileCreatedProvider;
	}

	public void setLocalFileCreatedProvider(Provider<LocalFileCreatedTask> localFileCreatedProvider) {
		this.localFileCreatedProvider = localFileCreatedProvider;
	}

	public Provider<LocalFileDeletedTask> getLocalFileDeletedProvider() {
		return localFileDeletedProvider;
	}

	public void setLocalFileDeletedProvider(Provider<LocalFileDeletedTask> localFileDeletedProvider) {
		this.localFileDeletedProvider = localFileDeletedProvider;
	}
}
