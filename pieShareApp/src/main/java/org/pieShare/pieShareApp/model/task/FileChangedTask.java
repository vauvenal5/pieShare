package org.pieShare.pieShareApp.model.task;

import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.model.message.FileChangedMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

public class FileChangedTask implements IPieEventTask<FileChangedMessage>
{

    private FileChangedMessage fileChangedMessage;
    private IFileService fileService;

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
