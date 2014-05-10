package org.pieShare.pieShareApp.model.task;

import org.pieShare.pieShareApp.model.message.FileTransferRequestMessage;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

public class FileTransferRequestTask implements IPieEventTask<FileTransferRequestMessage>
{

    private FileTransferRequestMessage fileTransferRequestMessage;
    private IFileService fileService;

    public FileTransferRequestMessage getFileTransferMessageBlocked()
    {
        return fileTransferRequestMessage;
    }

    public void setFileService(IFileService fileService)
    {
        this.fileService = fileService;
    }

    @Override
    public void setMsg(FileTransferRequestMessage msg)
    {
        this.fileTransferRequestMessage = msg;
    }

    @Override
    public void run()
    {
        fileService.fileTransferRequestReceived(fileTransferRequestMessage);
    }

}
