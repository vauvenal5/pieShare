package org.pieShare.pieShareApp.model.task;

import org.pieShare.pieShareApp.model.message.FileTransferMessageBlocked;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

public class FileTransferTask implements IPieEventTask<FileTransferMessageBlocked>
{

    private FileTransferMessageBlocked fileTransferMessageBlocked;
    private IFileService fileService;

    public FileTransferMessageBlocked getFileTransferMessageBlocked()
    {
        return fileTransferMessageBlocked;
    }

    public void setFileService(IFileService fileService)
    {
        this.fileService = fileService;
    }

    @Override
    public void setMsg(FileTransferMessageBlocked msg)
    {
        this.fileTransferMessageBlocked = msg;
    }

    @Override
    public void run()
    {
        fileService.fileTransfereMessage(fileTransferMessageBlocked);
    }

}
