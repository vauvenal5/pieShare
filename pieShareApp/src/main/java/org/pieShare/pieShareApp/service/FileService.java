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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.api.IFileMerger;
import org.pieShare.pieShareApp.api.IFileService;
import org.pieShare.pieShareApp.api.IFileWatcherService;
import org.pieShare.pieShareApp.configuration.Configuration;
import org.pieShare.pieShareApp.model.FileChangedMessage;
import org.pieShare.pieShareApp.model.task.FileChangedTask;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 * @author richy
 */
public class FileService implements IFileService
{

	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final PieLogger logger = new PieLogger(FileService.class);
	private IClusterService clusterService;
	private FileChangedTask fileChangedTask = null;
	private IFileMerger fileMerger = null;

	public FileService()
	{
		fileChangedTask = new FileChangedTask();
		fileChangedTask.setFileService(this);

		fileMerger = new FileMerger();

		try
		{
			registerAll(Configuration.getWorkingDirectory());
		}
		catch (IOException ex)
		{
			Logger.getLogger(FileService.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		addWatchDirectory(Configuration.getWorkingDirectory());
	}

	public void serClusterService(IClusterService clusterService)
	{
		this.clusterService = clusterService;
		clusterService.registerTask(FileChangedMessage.class, fileChangedTask);
	}

	private void addWatchDirectory(File file)
	{
		IFileWatcherService service = new ApacheFileWatcher();//new FileWatcherService();
		service.setFileMerger(fileMerger);
		service.setWatchDir(file);
		executor.execute(service);
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
}
