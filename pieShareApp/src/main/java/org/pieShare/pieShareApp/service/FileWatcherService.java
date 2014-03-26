package org.pieShare.pieShareApp.service;

import java.io.*;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import org.pieShare.pieShareApp.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

public class FileWatcherService implements Runnable
{

	private PieLogger logger = new PieLogger(FileWatcherService.class);
	private IFileService fileService;
	private WatchService watchService;
	private File watchDir;

	public FileWatcherService()
	{
		try
		{
			watchService = FileSystems.getDefault().newWatchService();
		}
		catch (IOException ex)
		{
			logger.error("Not able to init a FileWatcher. " + ex.getMessage());
			//ToDo. Handle Exception
		}
	}

	public void setFileService(IFileService fileService)
	{
		this.fileService = fileService;
	}

	public void setWatchDir(File watchDir)
	{
		this.watchDir = watchDir;
	}

	public void watchDir() throws InterruptedException, IOException
	{

		if (!watchDir.isDirectory())
		{
			//ToDo: Exception Handling
			logger.error("Watchdir is no Directory");
			return;
		}

		watchDir.toPath().register(watchService,
				StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_MODIFY,
				StandardWatchEventKinds.ENTRY_DELETE);	// Register the directory

		while (true)
		{
			WatchKey key = watchService.take();	// retrieve the watchkey
			for (WatchEvent event : key.pollEvents())
			{
				WatchEvent.Kind<?> kind = event.kind();

				if (kind == OVERFLOW)
				{
					continue;
				}

				//Changes in local directory. 
				logger.debug(event.kind() + ": " + event.context());

				Path newPath = ((WatchEvent<Path>) event).context();
				
				String relativeFilePath = event.context().toString();

				File changedFile = new File(watchDir, relativeFilePath);

				if (changedFile.isDirectory())
				{
					if (kind == ENTRY_CREATE)
					{
						fileService.newFolderAdded(changedFile);
					}
					continue;
				}

				PieFile pieFile = new PieFile(changedFile);

				if (kind == ENTRY_CREATE)
				{
					fileService.localFileAdded(pieFile);
				}
				if (kind == ENTRY_MODIFY)
				{
					fileService.localFileModified(pieFile);
				}
				if (kind == ENTRY_DELETE)
				{
					fileService.localFileDeleted(pieFile);
				}
			}
			boolean valid = key.reset();
			if (!valid)
			{
				break;	// Exit if directory is deleted
			}
		}
	}

	@Override
	public void run()
	{
		try
		{
			watchDir();
		}
		catch (InterruptedException | IOException ex)
		{
			logger.error("WatchService throwed an exception. " + ex.getMessage());
		}
	}
}
