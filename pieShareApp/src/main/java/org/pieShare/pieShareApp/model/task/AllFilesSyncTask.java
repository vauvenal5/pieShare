package org.pieShare.pieShareApp.model.task;

import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.model.message.AllFilesSyncMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

public class AllFilesSyncTask implements IPieEventTask<AllFilesSyncMessage>
{

	private AllFilesSyncMessage allFilesMessage;
	private IFileService fileService;

	public void setFileService(IFileService fileService)
	{
		this.fileService = fileService;
	}

	@Override
	public void setMsg(AllFilesSyncMessage msg)
	{
		this.allFilesMessage = msg;
	}

	@Override
	public void run()
	{
		fileService.remoteAllFilesSyncRequest(allFilesMessage);
	}

}
