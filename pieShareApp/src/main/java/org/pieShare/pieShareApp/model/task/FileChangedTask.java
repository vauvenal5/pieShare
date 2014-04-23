package org.pieShare.pieShareApp.model.task;

import org.pieShare.pieShareApp.api.IFileService;
import org.pieShare.pieShareApp.model.message.FileChangedMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class FileChangedTask implements IPieEventTask<FileChangedMessage>
{

    private FileChangedMessage fileChangedMessage;
    private IFileService fileService;

    @Autowired
    @Qualifier("fileService")
    public void setFileService(IFileService fileService)
    {
	this.fileService = fileService;
    }

    @Override
    public void setMsg(FileChangedMessage msg)
    {
	this.fileChangedMessage = msg;
    }

    @Override
    public void run()
    {
	fileService.remoteFileChange(fileChangedMessage);
    }

}
