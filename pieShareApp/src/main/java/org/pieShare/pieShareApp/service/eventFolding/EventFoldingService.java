/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.eventFolding;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Provider;
import org.pieShare.pieShareApp.model.LocalFileEvent;
import org.pieShare.pieShareApp.model.LocalFileEventType;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.eventFolding.event.ILocalFilderEventListener;
import org.pieShare.pieShareApp.service.eventFolding.event.LocalFilderEvent;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.historyService.IHistoryService;
import org.pieShare.pieShareApp.task.localTasks.EventFoldingTimerTask;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.AShutdownableService;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class EventFoldingService extends AShutdownableService implements IEventFoldingService, IShutdownableService {
	
	private IEventBase<ILocalFilderEventListener, LocalFilderEvent> localFilderEventBase;
	private ConcurrentHashMap<Integer , LocalFileEvent> localEvents;
	private IFileService fileService;
	private Timer timer;
        private IHashService hashService;
	private AtomicBoolean shutdown;
	private IHistoryService historyService;
        
	
	private Provider<EventFoldingTimerTask> eventFoldingTimerTaskProvider;
	
	public void init() {
		this.localEvents = new ConcurrentHashMap<Integer , LocalFileEvent>();
		this.timer = new Timer(true);
		this.shutdown = new AtomicBoolean(false);
	}

	@Override
	public void handleLocalEvent(LocalFileEvent event) {
		if(this.shutdown.get()) {
			return;
		}
		PieLogger.debug(this.getClass(), "Folding Service entered: " + "handleLocalEvent" + event.getType() + "file: " + event.getFile().getName());
                
		String relativePath = fileService.relativizeFilePath(event.getFile());
		long currentTime = (new Date()).getTime();
                LocalFileEventType type = event.getType();
                boolean eventConsumed = false;
                
                boolean dir = false;
                File eventFile = event.getFile();

                if(eventFile.exists()) {
                        dir = eventFile.isDirectory();
                } else {
                    PieFile res = this.historyService.getPieFile(fileService.relativizeFilePath(eventFile));
                    if(res == null) {
                            dir = true;
                    }
                }
                
                byte [] file = null;
                if(!dir) {
                    try {
                        if(type == LocalFileEventType.DELETED) {
                            //if deleted get MD5 from database
                            file = historyService.getPieFile(relativePath).getMd5();
                        }
                        else {
                            file  = hashService.hashStream(event.getFile());
                        }
                    } catch (IOException ex) {
                        PieLogger.debug(this.getClass(),"File hash create error: " +ex.toString());
                    }
                }
                byte [] name = hashService.hash(event.getFile().getName().getBytes());
                byte [] path = hashService.hash(relativePath.replace(event.getFile().getName(), "").getBytes());
                
              
                Integer relativePathHash;
                relativePathHash = Arrays.hashCode(hashService.hash(relativePath.getBytes()));
                
                //PieLogger.debug(this.getClass(), "Key contained: " + localEvents.containsKey(relativePathHash));
                synchronized(localEvents) {
                    if((type == LocalFileEventType.CREATED || type == LocalFileEventType.DELETED)
                            && !localEvents.containsKey(relativePathHash) && !dir) {
                        
                        byte [] baseOpposite = null;

                        if(type == LocalFileEventType.CREATED) {
                            baseOpposite = concatByteArray(file, hashService.hash(LocalFileEventType.DELETED.name().getBytes()));
                        }
                        else if(type == LocalFileEventType.DELETED) {
                            baseOpposite = concatByteArray(file, hashService.hash(LocalFileEventType.CREATED.name().getBytes()));
                        }


                        byte[] basePathOpposite = concatByteArray(baseOpposite, path);
                        byte[] baseNameOpposite = concatByteArray(baseOpposite, name);

                        Integer checkRename = Arrays.hashCode(hashService.hash(basePathOpposite));
                        Integer checkMove = Arrays.hashCode(hashService.hash(baseNameOpposite));
                        //Arrays.hashCode(a)
                        

                        if(localEvents.containsKey(checkRename)) {
                            Integer foldToRename = Arrays.hashCode(concatByteArray(file, hashService.hash(LocalFileEventType.RENAMED.name().getBytes())));

                            LocalFileEvent foldedEvent = new LocalFileEvent();
                            foldedEvent.setType(LocalFileEventType.RENAMED);
                            foldedEvent.setTimestamp(currentTime);
                            if(type == LocalFileEventType.CREATED) {
                                foldedEvent.setFile(event.getFile());
                                foldedEvent.setOldFile(localEvents.get(checkRename).getFile());
                            }
                            else {
                                foldedEvent.setFile(localEvents.get(checkRename).getFile());
                                foldedEvent.setOldFile(event.getFile());
                            }
                            
                            
                            //remove previously added relativepath event
                            relativePathHash = Arrays.hashCode(hashService.hash(fileService.relativizeFilePath(localEvents.get(checkRename).getFile()).getBytes()));
                            localEvents.remove(relativePathHash);
                            
                            //remove previously added move key
                            byte [] oldName = hashService.hash(foldedEvent.getOldFile().getName().getBytes());
                            Integer removeMove = Arrays.hashCode(hashService.hash(concatByteArray(baseOpposite, oldName)));
                            localEvents.remove(removeMove);
                            
                            localEvents.remove(checkRename);
                            localEvents.put(foldToRename, foldedEvent);
                            eventConsumed = true;
                            PieLogger.debug(this.getClass(), "Rename Fold: " + foldedEvent.getFile().toString());
                            
                        } else if(localEvents.containsKey(checkMove)) {
                            Integer foldToMove = Arrays.hashCode(concatByteArray(file, hashService.hash(LocalFileEventType.MOVED.name().getBytes())));

                            LocalFileEvent foldedEvent = new LocalFileEvent();
                            foldedEvent.setType(LocalFileEventType.MOVED);
                            foldedEvent.setTimestamp(currentTime);

                            if(type == LocalFileEventType.CREATED) {
                                foldedEvent.setFile(event.getFile());
                                foldedEvent.setOldFile(localEvents.get(checkMove).getFile());
                            }
                            else {
                                foldedEvent.setFile(localEvents.get(checkMove).getFile());
                                foldedEvent.setOldFile(event.getFile());
                            }

                            //remove previously added relativepath event
                            relativePathHash = Arrays.hashCode(hashService.hash(fileService.relativizeFilePath(localEvents.get(checkMove).getFile()).getBytes()));
                            localEvents.remove(relativePathHash);
                            
                            //remove previously added rename key
                            byte [] oldPath = hashService.hash(fileService.relativizeFilePath(foldedEvent.getOldFile()).replace(event.getFile().getName(), "").getBytes());
                            Integer removeRename = Arrays.hashCode(hashService.hash(concatByteArray(baseOpposite, oldPath)));
                            localEvents.remove(removeRename);
                            
                            localEvents.remove(checkMove);
                            localEvents.put(foldToMove, foldedEvent);
                            eventConsumed = true;
                            PieLogger.debug(this.getClass(), "Move Fold: " + foldedEvent.getFile().toString());

                        } else {
                            //if opposite event not contained -> add events to map
                            byte [] eventtype = hashService.hash(type.name().getBytes());
                            byte[] base = concatByteArray(file, eventtype);
                            

                            Integer addRename = Arrays.hashCode(hashService.hash(concatByteArray(base, path)));
                            Integer addMove = Arrays.hashCode(hashService.hash(concatByteArray(base, name)));
                            event.setTimestamp(currentTime);
                            localEvents.put(addRename, event);
                            localEvents.put(addMove, event);
                            PieLogger.debug(this.getClass(), "Event Folding add to list: " + event.getFile().toString());
                        }

                    }
                    //prevent adding a already folded event
                    if(!eventConsumed) {
                        
                        relativePathHash = Arrays.hashCode(hashService.hash(relativePath.getBytes()));
                        if(localEvents.containsKey(relativePathHash)) {
                            localEvents.get(relativePathHash).setTimestamp(currentTime);
                            

                            if((event.getType() == LocalFileEventType.MODIFIED) && !dir) {
                                
                                byte [] firstMD5 = localEvents.get(relativePathHash).getMD5();
                                byte[] removeBase = concatByteArray(firstMD5, hashService.hash(LocalFileEventType.CREATED.name().getBytes()));
                                Integer removeRename = Arrays.hashCode(hashService.hash(concatByteArray(removeBase, path)));
                                Integer removeMove = Arrays.hashCode(hashService.hash(concatByteArray(removeBase, name)));
                                
                                localEvents.remove(removeMove);
                                localEvents.remove(removeRename);
                                
                                return;
                            }
                        }
                        else {
                            event.setTimestamp(currentTime);
                            
                            if(!dir) {
                                try {
                                    if(type == LocalFileEventType.DELETED) {
                                        //if deleted get MD5 from database
                                        event.setMD5(historyService.getPieFile(relativePath).getMd5());
                                    }
                                    else {
                                        event.setMD5(hashService.hashStream(event.getFile()));
                                    }
                                } catch (IOException ex) {
                                    PieLogger.debug(this.getClass(),"File hash create error: " +ex.toString());
                                }
                            }
                            
                            localEvents.put(relativePathHash, event);
                        }
                    }
                }
                
            this.reschedule();
		
	}
        
        public byte [] concatByteArray(byte [] first, byte [] second) {
            byte[] result = new byte[first.length + second.length];
            System.arraycopy(first, 0, result, 0, first.length);
            System.arraycopy(second, 0, result, first.length, second.length);
            return result;
        }

	public ConcurrentHashMap<Integer , LocalFileEvent> getLocalEvents() {
		return localEvents;
	}

	public void reschedule() {
		if(this.shutdown.get()) {
			return;
		}
		
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

	public IEventBase<ILocalFilderEventListener, LocalFilderEvent> getLocalFilderEventBase() {
		return localFilderEventBase;
	}

	public void setLocalFilderEventBase(IEventBase<ILocalFilderEventListener, LocalFilderEvent> localFilderEventBase) {
		this.localFilderEventBase = localFilderEventBase;
	}
        
        public void setHistoryService(IHistoryService historyService) {
		this.historyService = historyService;
	}

	@Override
	public void shutdown() {
		this.shutdown.set(true);
		this.timer.cancel();
	}

        public void setHashService(IHashService hashService) {
            this.hashService = hashService;
        }
        
        
        
        
}
