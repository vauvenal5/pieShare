/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.eventFolding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Provider;
import org.pieShare.pieShareApp.model.LocalFileEvent;
import org.pieShare.pieShareApp.model.LocalFileEventType;
import org.pieShare.pieShareApp.service.eventFolding.event.ILocalFilderEventListener;
import org.pieShare.pieShareApp.service.eventFolding.event.LocalFilderEvent;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.task.localTasks.EventFoldingTimerTask;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class EventFoldingService implements IEventFoldingService {
	
	private IEventBase<ILocalFilderEventListener, LocalFilderEvent> localFilderEventBase;
	private ConcurrentHashMap<byte [], LocalFileEvent> localEvents;
	private IFileService fileService;
	private Timer timer;
        private IHashService hashService;
        
	
	private Provider<EventFoldingTimerTask> eventFoldingTimerTaskProvider;
	
	public void init() {
		this.localEvents = new ConcurrentHashMap<byte [], LocalFileEvent>();
		this.timer = new Timer(true);
	}

	@Override
	public void handleLocalEvent(LocalFileEvent event) {
		String relativePath = fileService.relativizeFilePath(event.getFile());
		long currentTime = (new Date()).getTime();
                LocalFileEventType type = event.getType();
                boolean eventConsumed = false;
                
                byte [] file = null;
                try {
                   file  = hashService.hashStream(event.getFile());
                } catch (IOException ex) {
                    Logger.getLogger(EventFoldingService.class.getName()).log(Level.SEVERE, null, ex);
                }
                byte [] name = hashService.hash(event.getFile().getName().getBytes());
                byte [] path = hashService.hash(fileService.relativizeFilePath(event.getFile()).replace(event.getFile().getName(), "").getBytes());
                
                byte [] eventtype = hashService.hash(event.getType().name().getBytes());
                byte[] baseOpposite = null;
                
                if(type == LocalFileEventType.CREATED) {
                    baseOpposite = concatByteArray(file, hashService.hash(LocalFileEventType.DELETED.name().getBytes()));
                }
                else if(type == LocalFileEventType.DELETED) {
                    baseOpposite = concatByteArray(file, hashService.hash(LocalFileEventType.CREATED.name().getBytes()));
                }
                
                
                byte[] basePathOpposite = concatByteArray(baseOpposite, path);
                byte[] baseNameOpposite = concatByteArray(baseOpposite, name);
                
                byte [] checkRename = hashService.hash(basePathOpposite);
                byte [] checkMove = hashService.hash(baseNameOpposite);
              
                byte[] relativePathHash;
                relativePathHash = hashService.hash(relativePath.getBytes());
                
                synchronized(localEvents) {
                    if((type == LocalFileEventType.CREATED || type == LocalFileEventType.DELETED)
                            && !localEvents.containsKey(relativePathHash)) {

                        if(localEvents.containsKey(checkRename)) {
                            byte[] foldToRename = concatByteArray(file, hashService.hash(LocalFileEventType.RENAMED.name().getBytes()));

                            LocalFileEvent foldedEvent = new LocalFileEvent();
                            foldedEvent.setType(LocalFileEventType.RENAMED);
                            foldedEvent.setTimestamp(currentTime);
                            if(type == LocalFileEventType.CREATED) {
                                foldedEvent.setNewFile(event.getFile());
                                foldedEvent.setOldFile(localEvents.get(checkRename).getFile());
                            }
                            else {
                                foldedEvent.setNewFile(localEvents.get(checkRename).getFile());
                                foldedEvent.setOldFile(event.getFile());
                            }
                            
                            
                            //remove previously added relativepath event
                            relativePathHash = hashService.hash(fileService.relativizeFilePath(localEvents.get(checkRename).getFile()).getBytes());
                            localEvents.remove(relativePathHash);
                            
                            //remove previously added move key
                            byte [] oldName = hashService.hash(foldedEvent.getOldFile().getName().getBytes());
                            byte [] removeMove = hashService.hash(concatByteArray(baseOpposite, oldName));
                            localEvents.remove(removeMove);
                            
                            localEvents.remove(checkRename);
                            localEvents.put(foldToRename, foldedEvent);
                            eventConsumed = true;
                            
                        } else if(localEvents.containsKey(checkMove)) {
                            byte[] foldToMove = concatByteArray(file, hashService.hash(LocalFileEventType.MOVED.name().getBytes()));

                            LocalFileEvent foldedEvent = new LocalFileEvent();
                            foldedEvent.setType(LocalFileEventType.MOVED);
                            foldedEvent.setTimestamp(currentTime);

                            if(type == LocalFileEventType.CREATED) {
                                foldedEvent.setNewFile(event.getFile());
                                foldedEvent.setOldFile(localEvents.get(checkMove).getFile());
                            }
                            else {
                                foldedEvent.setNewFile(localEvents.get(checkMove).getFile());
                                foldedEvent.setOldFile(event.getFile());
                            }

                            //remove previously added relativepath event
                            relativePathHash = hashService.hash(fileService.relativizeFilePath(localEvents.get(checkMove).getFile()).getBytes());
                            localEvents.remove(relativePathHash);
                            
                            //remove previously added rename key
                            byte [] oldPath = hashService.hash(fileService.relativizeFilePath(foldedEvent.getOldFile()).replace(event.getFile().getName(), "").getBytes());
                            byte [] removeRename = hashService.hash(concatByteArray(baseOpposite, oldPath));
                            localEvents.remove(removeRename);
                            
                            localEvents.remove(checkMove);
                            localEvents.put(foldToMove, foldedEvent);
                            eventConsumed = true;

                        } else {
                            //if opposite event not contained -> add events to map
                            byte[] base = concatByteArray(file, eventtype);

                            byte[] addRename = hashService.hash(concatByteArray(base, path));
                            byte[] addMove = hashService.hash(concatByteArray(base, name));
                            event.setTimestamp(currentTime);
                            localEvents.put(addRename, event);
                            localEvents.put(addMove, event);

                        }

                        

                    }
                    //prevent adding a already folded event
                    if(!eventConsumed) {
                        
                        relativePathHash = hashService.hash(relativePath.getBytes());
                        if(localEvents.containsKey(relativePathHash)) {
                            localEvents.get(relativePathHash).setTimestamp(currentTime);
                            

                            if(event.getType() == LocalFileEventType.MODIFIED) {
                                
                                byte [] firstMD5 = localEvents.get(relativePathHash).getMD5();
                                byte[] removeBase = concatByteArray(firstMD5, hashService.hash(LocalFileEventType.CREATED.name().getBytes()));
                                byte[] removeRename = hashService.hash(concatByteArray(removeBase, path));
                                byte[] removeMove = hashService.hash(concatByteArray(removeBase, name));
                                if(localEvents.containsKey(path)) {
                                    localEvents.remove(removeMove);
                                    localEvents.remove(removeRename);
                                }
                                return;
                            }
                        }
                        else {
                            event.setTimestamp(currentTime);
                            try {
                                event.setMD5(hashService.hashStream(event.getFile()));
                            } catch (IOException ex) {
                                Logger.getLogger(EventFoldingService.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            localEvents.put(relativePathHash, event);
                        }
                    }
                }
                
                
		/*
			if(this.localEvents.containsKey(relativePath)) {
                                //can be used for create, rename of files, delte
                            
				//update the timestamp of the existing event
				//this.localEvents.get(relativePath).setTimestamp(currentTime);
                                LocalFileEvent prev = this.localEvents.get(relativePath);
				//if we allready have an event for the given file and it is 
				//being modified then we can ignore the new event
				if(event.getType() == LocalFileEventType.MODIFIED) {
					return;
				}
                                else {
                                    prev.add(event);
                                    try {
                                        fileService.getPieFile(event.getFile());
                                    } catch (IOException ex) {
                                        Logger.getLogger(EventFoldingService.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    return;
				}
                                

				//for now we do not handle any other events
				
			}
                        else {
                            event.setTimestamp(currentTime);
                            //this.localEvents.put(relativePath, event);
                        }
	*/
            this.reschedule();
		
	}
        
        public byte [] concatByteArray(byte [] first, byte [] second) {
            byte[] result = new byte[first.length + second.length];
            System.arraycopy(first, 0, result, 0, first.length);
            System.arraycopy(second, 0, result, first.length, second.length);
            return result;
        }

	public ConcurrentHashMap<byte [], LocalFileEvent> getLocalEvents() {
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

	public IEventBase<ILocalFilderEventListener, LocalFilderEvent> getLocalFilderEventBase() {
		return localFilderEventBase;
	}

	public void setLocalFilderEventBase(IEventBase<ILocalFilderEventListener, LocalFilderEvent> localFilderEventBase) {
		this.localFilderEventBase = localFilderEventBase;
	}

        public void setHashService(IHashService hashService) {
            this.hashService = hashService;
        }
        
        
        
        
}
