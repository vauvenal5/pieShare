package org.pieShare.pieShareApp.service;

import java.io.*;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import org.pieShare.pieShareApp.api.IFileService;
import org.pieShare.pieShareApp.api.IFileWatcherService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

public class FileWatcherService implements IFileWatcherService
{

    private PieLogger logger = new PieLogger(FileWatcherService.class);
    private IFileService fileService;
    private WatchService watchService;
    private File watchDir;
    private HashMap<String, PieFile> files = null;
    private WatchKey key;

    public FileWatcherService()
    {
	files = new HashMap<>();

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

    @Override
    public void setFileService(IFileService fileService)
    {
	this.fileService = fileService;
    }

    @Override
    public void setWatchDir(File watchDir)
    {
	this.watchDir = watchDir;
    }

    @Override
    public void watchDir() throws IOException
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

	
	registerAll(watchDir.toPath());
	
	while (true)
	{
	    try
	    {
		key = watchService.take();	// retrieve the watchkey
	    }
	    catch (InterruptedException ex)
	    {
		logger.debug("Watcher Key Interrupted Dir: " + watchDir.getPath());
	    }

	    // Path dir = keys.get(key);
	    for (WatchEvent event : key.pollEvents())
	    {
		WatchEvent.Kind<?> kind = event.kind();

		if (kind == OVERFLOW)
		{
		    continue;
		}

		//Changes in local directory. 
		logger.debug(event.kind() + ": " + event.context());

		String relativeFilePath = event.context().toString();

		File changedFile = new File(watchDir, relativeFilePath);

		if (changedFile.isDirectory())
		{
		   
		}

		if (!changedFile.exists() && kind == ENTRY_CREATE)
		{
		    logger.debug("Watched File does not exist, maby triggered by an old watcher. " + event + ": File: " + changedFile.getPath());
		    continue;
		}

		PieFile pieFile = new PieFile(changedFile);

		if (kind == ENTRY_CREATE)
		{
		    System.gc();
		    fileService.localFileAdded(pieFile);
		}
		if (kind == ENTRY_MODIFY)
		{
		    System.gc();
		    fileService.localFileModified(pieFile);
		}
		if (kind == ENTRY_DELETE)
		{
		    System.gc();
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

    private void registerAll(final Path start) throws IOException
    {
	// register directory and sub-directories
	Files.walkFileTree(start, new SimpleFileVisitor<Path>()
	{
	    @Override
	    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
		    throws IOException
	    {
		fileService.newFolderAdded(dir.toFile());
		//dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		return FileVisitResult.CONTINUE;
	    }
	});
    }

    @Override
    public void cancel()
    {
	if (key != null)
	{
	    key.cancel();
	}
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
	    logger.error("WatchService throwed an exception. " + ex.getMessage());
	}
    }
}
