/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service;

import java.io.File;
import java.io.IOException;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.pieShare.pieShareApp.api.IFileMerger;
import org.pieShare.pieShareApp.api.IFileWatcherService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class ApacheFileWatcher implements IFileWatcherService
{

	private PieLogger logger = new PieLogger(ApacheFileWatcher.class);
	private IFileMerger fileMerger;
	private File watchDir;

	@Override
	public void setFileMerger(IFileMerger fileMerger)
	{
		this.fileMerger = fileMerger;
	}

	@Override
	public void setWatchDir(File watchDir)
	{
		this.watchDir = watchDir;
	}

	@Override
	public void watchDir() throws IOException
	{

		FileSystemManager fileSystemManager = VFS.getManager();
		FileObject dirToWatchFO = null;
		dirToWatchFO = fileSystemManager.resolveFile(watchDir.getAbsolutePath());

		DefaultFileMonitor fileMonitor = new DefaultFileMonitor(new FileListener()
		{
			@Override
			public void fileCreated(FileChangeEvent fce) throws Exception
			{
				String filePath = fce.getFile().getURL().getFile();
				PieFile pieFile = new PieFile(filePath);
				
				fileMerger.fileCreated(new File(filePath));
				
			}

			@Override
			public void fileDeleted(FileChangeEvent fce) throws Exception
			{
				String filePath = fce.getFile().getURL().getFile();
				fileMerger.fileDeleted(new File(filePath));
			}

			@Override
			public void fileChanged(FileChangeEvent fce) throws Exception
			{
				String filePath = fce.getFile().getURL().getFile();
				fileMerger.fileChanged(new File(filePath));
			}
		});

		fileMonitor.setRecursive(true);
		fileMonitor.addFile(dirToWatchFO);
		fileMonitor.start();

	}

	@Override
	public void cancel()
	{
	}

	@Override
	public void deleteAll()
	{
	}

	@Override
	public void run()
	{
		try
		{
			watchDir();
		}
		catch (IOException ex)
		{
		    logger.debug("Watcher error: Message: " + ex.getMessage());
		}
	}
}
