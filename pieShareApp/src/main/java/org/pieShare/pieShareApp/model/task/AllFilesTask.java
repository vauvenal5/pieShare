package org.pieShare.pieShareApp.model.task;

import org.pieShare.pieShareApp.api.IFileService;
import org.pieShare.pieShareApp.model.AllFilesMessage;
import org.pieShare.pieShareApp.model.FileChangedMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AllFilesTask implements IPieEventTask<AllFilesMessage>
{

	private AllFilesMessage allFilesMessage;
	private IFileService fileService;

	@Autowired
	@Qualifier("fileService")
	public void setFileService(IFileService fileService)
	{
		this.fileService = fileService;
	}

	@Override
	public void setMsg(AllFilesMessage msg)
	{
		this.allFilesMessage = msg;
	}

	@Override
	public void run()
	{
		//fileService.remoteFileChange(fileChangedMessage);
	}

}
