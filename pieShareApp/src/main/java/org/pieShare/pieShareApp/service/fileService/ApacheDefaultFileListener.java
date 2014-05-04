/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.pieShare.pieShareApp.service.fileService.api.IFileObserver;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;

/**
 *
 * @author richy
 */
public class ApacheDefaultFileListener implements FileListener
{

	private IFileObserver fileObserver;
	private IExecutorService executerService;
	
	public void setFileObserver(IFileObserver fileObserver)
	{
		this.fileObserver = fileObserver;
	}
	
	public void setExecutorService(IExecutorService executerService)
	{
		this.executerService = executerService;
	}

	@Override
	public void fileCreated(FileChangeEvent fce) throws Exception
	{
		String filePath = fce.getFile().getURL().getFile();
		startObservation(new File(filePath), FileChangedTypes.FILE_CREATED);
	}

	@Override
	public void fileDeleted(FileChangeEvent fce) throws Exception
	{
		String filePath = fce.getFile().getURL().getFile();
		startObservation(new File(filePath), FileChangedTypes.FILE_DELETED);
	}

	@Override
	public void fileChanged(FileChangeEvent fce) throws Exception
	{
		String filePath = fce.getFile().getURL().getFile();
		startObservation(new File(filePath), FileChangedTypes.FILE_MODIFIED);
	}

	private void startObservation(File file, FileChangedTypes event)
	{
		fileObserver.setData(file, event);
		executerService.execute(fileObserver);
	}

}
