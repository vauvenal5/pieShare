package org.pieShare.pieShareApp.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.api.IFileMerger;
import org.pieShare.pieShareApp.api.IFileService;
import org.pieShare.pieShareApp.api.IFileWatcherService;
import org.pieShare.pieShareApp.configuration.Configuration;
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

    public FileService()
    {
    }

    @PostConstruct
    public void initFileService()
    {
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
}
