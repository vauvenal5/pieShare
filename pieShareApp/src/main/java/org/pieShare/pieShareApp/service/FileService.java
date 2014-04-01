package org.pieShare.pieShareApp.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.pieShare.pieShareApp.api.IFileService;
import org.pieShare.pieShareApp.api.IFileWatcherService;
import org.pieShare.pieShareApp.configuration.Configuration;
import org.pieShare.pieShareApp.model.FileChangedMessage;
import org.pieShare.pieShareApp.model.task.FileChangedTask;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;

/**
 * @author richy
 */
public class FileService implements IFileService
{

	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final PieLogger logger = new PieLogger(FileService.class);
	private IClusterService clusterService;
	private FileChangedTask fileChangedTask = null;
	private HashMap<String, PieFile> files = null;
	private HashMap<String, IFileWatcherService> watchServices = null;

	public FileService()
	{
		fileChangedTask = new FileChangedTask();
		fileChangedTask.setFileService(this);
		files = new HashMap<String, PieFile>();
		watchServices = new HashMap<>();

		try
		{
			registerAll(Configuration.getWorkingDirectory());
		}
		catch (IOException ex)
		{
			logger.debug("Exception while registering Folders.");
		}
	}

	public void serClusterService(IClusterService clusterService)
	{
		this.clusterService = clusterService;
		clusterService.registerTask(FileChangedMessage.class, fileChangedTask);
	}

	@Override
	public void newFolderAdded(File file)
	{
		IFileWatcherService service = new FileWatcherService();//new FileWatcherService();
		service.setFileService(this);
		service.setWatchDir(file);
		executor.execute(service);
		watchServices.put(new PieFile(file).getRelativeFilePath(), service);
	}

	@Override
	public void folderRemoved(PieFile file)
	{
		if (watchServices.containsKey(file.getRelativeFilePath()))
		{
			watchServices.get(file.getRelativeFilePath()).deleteAll();
			watchServices.remove(file.getRelativeFilePath());
		}
	}

	public void remoteFileChanged(FileChangedMessage message)
	{
		PieFile remoteFile = message.getPieFile();

		if (message.getFileChangedType() == FileChangedTypes.FILE_CREATED)
		{
			remoteFileAdded(remoteFile);
		}
		if (message.getFileChangedType() == FileChangedTypes.FILE_MODIFIED)
		{
			remoteFileModified(remoteFile);
		}
		if (message.getFileChangedType() == FileChangedTypes.FILE_DELETED)
		{
			remoteFileDeleted(remoteFile);
		}
	}

	private void remoteFileAdded(PieFile remoteFile)
	{
		if (files.containsKey(remoteFile.getRelativeFilePath()))
		{
			PieFile localFile = files.get(remoteFile.getRelativeFilePath());

			if (localFile.equals(remoteFile))
			{
				//Conflict.... Error Error Error
			}
		}
		else
		{
			//Retrieve file from Remote Host
		}
	}

	private void remoteFileDeleted(PieFile remoteFile)
	{
		if (files.containsKey(remoteFile.getRelativeFilePath()))
		{
			PieFile localFile = files.get(remoteFile.getRelativeFilePath());

			if (localFile.equals(remoteFile))
			{
				//Delete localfile
			}
			else
			{
				//Conflict ...
			}
		}
	}

	private void remoteFileModified(PieFile remoteFile)
	{
		if (files.containsKey(remoteFile.getRelativeFilePath()))
		{
			PieFile localFile = files.get(remoteFile.getRelativeFilePath());

			if (remoteFile.getLastModified() > localFile.getLastModified())
			{
				//RemoteFile is newer, get the new file.
			}
			else if (remoteFile.getLastModified() < localFile.getLastModified())
			{
				//Conflict, local file is newer than remote
			}
			else if (remoteFile.equals(localFile))
			{
				//Strange, is same file... check for conflict
			}
		}
		else
		{
			//File does no Exist, retrieve from remote
		}
	}

	@Override
	public void registerAll(File file) throws IOException
	{
		// register directory and sub-directories
		Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
					throws IOException
			{
				newFolderAdded(dir.toFile());
				//dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
				return FileVisitResult.CONTINUE;
			}

		});

	}
}
