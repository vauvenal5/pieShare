/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import javax.inject.Provider;
import org.pieShare.pieShareApp.model.LocalFileEvent;
import org.pieShare.pieShareApp.model.LocalFileEventType;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.eventFolding.EventFoldingService;
import org.pieShare.pieShareApp.service.eventFolding.event.LocalFilderEvent;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.historyService.IHistoryService;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileChangedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileCreatedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileDeletedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileMovedTask;
import org.pieShare.pieShareApp.task.localTasks.fileEventTask.LocalFileRenamedTask;
import org.pieShare.pieShareApp.task.localTasks.folderEventTask.LocalFolderCreatedTask;
import org.pieShare.pieShareApp.task.localTasks.folderEventTask.LocalFolderDeletedTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class EventFoldingTimerTask extends TimerTask {
	
	private IExecutorService executorService;
	private EventFoldingService eventFoldingService;
	private IHistoryService historyService;
        private IFileService fileService;
        private IHashService hashService;
	
	private Provider<LocalFileChangedTask> localFileChangedProvider;
	private Provider<LocalFileCreatedTask> localFileCreatedProvider;
	private Provider<LocalFileDeletedTask> localFileDeletedProvider;
        private Provider<LocalFileRenamedTask> localFileRenamedProvider;
        private Provider<LocalFileMovedTask> localFileMovedProvider;
	
	private Provider<LocalFolderCreatedTask> localFolderCreatedProvider;
	private Provider<LocalFolderDeletedTask> localFolderDeletedProvider;

	@Override
	public void run() {
		Map<byte [], LocalFileEvent> localEvents = this.eventFoldingService.getLocalEvents();
                ArrayList<LocalFileEvent> consumedDeletes = new ArrayList<>();
		
		synchronized (localEvents) {
			if (localEvents.isEmpty()) {
				return;
			}

			long currentTime = new Date().getTime();

			Iterator<Map.Entry<byte [], LocalFileEvent>> iterator = localEvents.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<byte [], LocalFileEvent> entry = iterator.next();

				if ((currentTime - entry.getValue().getTimestamp()) > 2000) {
					LocalFilderEvent filderEvent = new LocalFilderEvent(this, entry.getValue());
					this.eventFoldingService.getLocalFilderEventBase().fireEvent(filderEvent);
                                        
                                        ALocalEventTask task = null;
					
					boolean dir = false;
					File file = entry.getValue().getFile();
					
					if(file.exists()) {
						dir = file.isDirectory();
					} else {
						PieFile res = this.historyService.getPieFileFromHistory(file);
						if(res == null) {
							dir = true;
						}
					}
					
                                        //TODO: add rename and move to switch
					switch (entry.getValue().getType()) {
						case CREATED:
							if(dir) {
								task = localFolderCreatedProvider.get();
								break;
							}
                                                        
							task = localFileCreatedProvider.get();
							break;
						case DELETED:
                                                        consumedDeletes.add(entry.getValue());
							if(dir) {
								task = localFolderDeletedProvider.get();
								break;
							}
							
							task = localFileDeletedProvider.get();
							break;
						default:
							if(dir) {
								//not yet implemented
								break;
							}
							task = localFileChangedProvider.get();
							break;
					}

					task.setFile(file);
					executorService.execute(task);
					iterator.remove();
				}
			}
                       
                        //remove unneccessary rename and move events for delete
                        if(!consumedDeletes.isEmpty()) {
                            for(LocalFileEvent ev : consumedDeletes) {
                                byte [] name = hashService.hash(ev.getFile().getName().getBytes());
                                byte [] path = hashService.hash(fileService.relativizeFilePath(ev.getFile()).replace(ev.getFile().getName(), "").getBytes());
                            
                                byte [] firstMD5 = ev.getMD5();
                                byte[] removeBase = concatByteArray(firstMD5, hashService.hash(LocalFileEventType.DELETED.name().getBytes()));
                                byte[] removeRename = hashService.hash(concatByteArray(removeBase, path));
                                byte[] removeMove = hashService.hash(concatByteArray(removeBase, name));
                                if(localEvents.containsKey(path)) {
                                    localEvents.remove(removeMove);
                                    localEvents.remove(removeRename);
                                }
                            }
                        }
			
			if (!localEvents.isEmpty()) {
				this.eventFoldingService.reschedule();
			}
		}
	}
        
        public byte [] concatByteArray(byte [] first, byte [] second) {
            byte[] result = new byte[first.length + second.length];
            System.arraycopy(first, 0, result, 0, first.length);
            System.arraycopy(second, 0, result, first.length, second.length);
            return result;
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

	public void setHistoryService(IHistoryService historyService) {
		this.historyService = historyService;
	}

	public void setLocalFolderCreatedProvider(Provider<LocalFolderCreatedTask> localFolderCreatedProvider) {
		this.localFolderCreatedProvider = localFolderCreatedProvider;
	}

	public void setLocalFolderDeletedProvider(Provider<LocalFolderDeletedTask> localFolderDeletedProvider) {
		this.localFolderDeletedProvider = localFolderDeletedProvider;
	}
        
        public void setLocalFileRenamedProvider(Provider<LocalFileRenamedTask> localFileRenamedProvider) {
		this.localFileRenamedProvider = localFileRenamedProvider;
	}

        public void setLocalFileMovedProvider(Provider<LocalFileMovedTask> localFileMovedProvider) {
            this.localFileMovedProvider = localFileMovedProvider;
        }
        public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

        public void setHashService(IHashService hashService) {
            this.hashService = hashService;
        }
        
        
        
}
