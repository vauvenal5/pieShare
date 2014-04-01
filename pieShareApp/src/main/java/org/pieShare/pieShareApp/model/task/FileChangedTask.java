package org.pieShare.pieShareApp.model.task;

import org.pieShare.pieShareApp.api.IFileService;
import org.pieShare.pieShareApp.model.FileChangedMessage;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.model.task.api.IMessageTask;


public class FileChangedTask implements IMessageTask {

    private FileChangedMessage fileChangedMessage;
    private IFileService fileService;
 
    public void setFileService(IFileService fileService)
    {
        this.fileService = fileService;
    }
    
    @Override
    public void setMsg(IPieMessage msg) {
        this.fileChangedMessage = (FileChangedMessage)msg;
    }

    @Override
    public void run() {
      // fileService.remoteFileChanged(fileChangedMessage);
    }
}