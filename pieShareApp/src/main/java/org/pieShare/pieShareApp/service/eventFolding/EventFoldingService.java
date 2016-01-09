/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.eventFolding;

import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Provider;
import org.pieShare.pieShareApp.model.LocalFileEvent;
import org.pieShare.pieShareApp.model.LocalFileEventType;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.task.localTasks.EventFoldingTimerTask;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class EventFoldingService implements IEventFoldingService {
	
	private ConcurrentHashMap<String, LocalFileEvent> localEvents;
	private IFileService fileService;
	private Timer timer;
	
	private Provider<EventFoldingTimerTask> eventFoldingTimerTaskProvider;
	
	public void init() {
		this.localEvents = new ConcurrentHashMap<String, LocalFileEvent>();
		this.timer = new Timer(true);
	}

	@Override
	public void handleLocalEvent(LocalFileEvent event) {
		String relativePath = fileService.relativizeFilePath(event.getFile());
		long currentTime = (new Date()).getTime();
		
		synchronized(localEvents) {
			if(this.localEvents.containsKey(relativePath)) {
				//update the timestamp of the existing event
				this.localEvents.get(relativePath).setTimestamp(currentTime);

				//if we allready have an event for the given file and it is 
				//being modified then we can ignore the new event
				if(event.getType() == LocalFileEventType.MODIFIED) {
					return;
				}

				//for now we do not handle any other events
				return;
			}

			event.setTimestamp(currentTime);
			this.localEvents.put(relativePath, event);
			this.reschedule();
		}
	}

	public ConcurrentHashMap<String, LocalFileEvent> getLocalEvents() {
		return localEvents;
	}

	public void reschedule() {
		EventFoldingTimerTask task = this.eventFoldingTimerTaskProvider.get();
		task.setEventFoldingService(this);
		this.timer.schedule(task, 2000);
	}

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	public void setEventFoldingTimerTaskProvider(Provider<EventFoldingTimerTask> eventFoldingTimerTaskProvider) {
		this.eventFoldingTimerTaskProvider = eventFoldingTimerTaskProvider;
	}
}
