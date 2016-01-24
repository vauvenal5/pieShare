/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.task.localTasks;

import java.io.File;
import javax.inject.Provider;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileObject;
import org.pieShare.pieShareApp.model.LocalFileEvent;
import org.pieShare.pieShareApp.model.LocalFileEventType;
import org.pieShare.pieShareApp.service.eventFolding.IEventFoldingService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Chris
 */
public class EventFoldingServiceTask implements IPieTask {
    
    private IEventFoldingService eventFoldingService;
    private Provider<LocalFileEvent> localFileEventProvider;
    private LocalFileEventType type;
    private FileChangeEvent fce;
    
    @Override
    public void run() {
        PieLogger.info(this.getClass(), "File event {} for: {}", type.toString(), fce.getFile().getName().getPath());
        LocalFileEvent event = this.localFileEventProvider.get();
        event.setType(type);
        event.setFile(this.convertFileObject(fce.getFile()));
        this.eventFoldingService.handleLocalEvent(event);
    }

    public void init(LocalFileEventType type, FileChangeEvent fce) {
        this.type = type;
        this.fce = fce;
    }
    
    private File convertFileObject(FileObject object) {
        return new File(object.getName().getPath());
    }
    
    public void setEventFoldingService(IEventFoldingService eventFoldingService) {
        this.eventFoldingService = eventFoldingService;
    }
    
    public void setLocalFileEventProvider(Provider<LocalFileEvent> localFileEventProvider) {
        this.localFileEventProvider = localFileEventProvider;
    }
}
