package org.pieShare.pieShareApp.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.api.IFileMerger;
import org.pieShare.pieShareApp.api.IFileService;
import org.pieShare.pieShareApp.api.IFileWatcherService;
import org.pieShare.pieShareApp.configuration.Configuration;
import org.pieShare.pieShareApp.model.AllFilesMessage;
import org.pieShare.pieShareApp.model.FileChangedMessage;
import org.pieShare.pieShareApp.model.task.FileChangedTask;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;

/**
 * @author richy
 */
public class FileService implements IFileService
{

	private final PieLogger logger = new PieLogger(FileService.class);
	private IClusterService clusterService;
	private IFileMerger fileMerger = null;
	private IExecutorService executorService = null;
	private ArrayList<UUID> pendingTasks;

	public FileService()
	{
	}

	@PostConstruct
	public void initFileService()
	{
		pendingTasks = new ArrayList<>();
		
		try
		{
			registerAll(Configuration.getWorkingDirectory());
		}
		catch (IOException ex)
		{
			logger.error("Error parsing workingDir at startup");
		}

		addWatchDirectory(Configuration.getWorkingDirectory());
	}

	public void setClusterService(IClusterService clusterService)
	{
		this.clusterService = clusterService;
	}

	public void setExecutorService(IExecutorService executorService)
	{
		this.executorService = executorService;
		this.executorService.registerTask(FileChangedMessage.class, FileChangedTask.class);
	}

	public void setFileMerger(IFileMerger fileMerger)
	{
		this.fileMerger = fileMerger;
		fileMerger.setFileService(this);
	}

	private void addWatchDirectory(File file)
	{
		IFileWatcherService service = new ApacheFileWatcher();//new FileWatcherService();
		service.setFileMerger(fileMerger);
		service.setWatchDir(file);

		executorService.execute(service);
	}

	private void registerAll(File file) throws IOException
	{
		Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				fileMerger.fileCreated(file.toFile());
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
			{
				fileMerger.fileCreated(dir.toFile());
				return FileVisitResult.CONTINUE;
			}
		});
	}

	@Override
	public void remoteFileChange(FileChangedMessage message)
	{
		File wdir = Configuration.getWorkingDirectory();
		File newFile = new File(wdir, message.getRelativeFilePath());

		if (message.getChangedType() == FileChangedTypes.FILE_CREATED)
		{
			fileMerger.fileCreated(newFile);
		}
		else if (message.getChangedType() == FileChangedTypes.FILE_DELETED)
		{
			fileMerger.fileDeleted(newFile);
		}
		else if (message.getChangedType() == FileChangedTypes.FILE_MODIFIED)
		{
			fileMerger.fileChanged(newFile);
		}
	}

	@Override
	public void localFileChange(FileChangedMessage message)
	{
		try
		{
			clusterService.sendMessage(message);
		}
		catch (ClusterServiceException ex)
		{
			logger.error("Error sending file changed message: " + ex.getMessage());
		}
	}

	@Override
	public void remoteAllFilesRequestArrvied(AllFilesMessage msg)
	{
		if (msg.isIsRequest())
		{
			logger.debug("File Service: New AllFilesMessage request arrvied");

			if (msg.getId() == null)
			{
				logger.debug("File Service: Arrived AllFilesMessage request had no ID. Return.");
			}

			AllFilesMessage sendMsg = new AllFilesMessage();
			sendMsg.setId(msg.getId());
			sendMsg.setIsRequest(false);
			sendMsg.setDirs(fileMerger.getDirs());
			try
			{
				clusterService.sendMessage(sendMsg);
			}
			catch (ClusterServiceException ex)
			{
				logger.debug("File Service: Error sending AllFilesMessage request. Message: " + ex.getMessage());
			}

			logger.debug("File Service: AllFilesMessage request sended");
		}
		else
		{
			logger.debug("File Service: New AllFilesMessage arrvied");
			
			if(msg.getId() == null || msg.getDirs() == null)
			{
				//ToDo: When id not null, but dirs, check if id is in pending tasks.
				logger.debug("File Service: Error in Message, either no ID or Dirs. Return");
				return;
			}
			
			if(!pendingTasks.contains(msg.getId()))
			{
				return;
			}
			
			pendingTasks.remove(msg.getId());
			
			for(PieDirectory dir : msg.getDirs().values())
			{
				for(PieFile file : dir.getFiles().values())
				{
					File newFile = new File(Configuration.getWorkingDirectory(), file.getRelativeFilePath());
					fileMerger.fileChanged(newFile);
				}
			}
			
		}

	}

	@Override
	public void sendAllFilesRequest()
	{
		logger.debug("File Service: Send new AllFilesRequest");
		UUID id = UUID.randomUUID();
		
		AllFilesMessage msg = new AllFilesMessage();
		msg.setIsRequest(true);
		msg.setId(id);
		pendingTasks.add(id);
		try
		{
			clusterService.sendMessage(msg);
		}
		catch (ClusterServiceException ex)
		{
			logger.debug("File Service: Error sending new AllFilesRequest. Message: " + ex.getMessage());
		}
		logger.debug("File Service: AllFilesRequest sended");
	}
}
