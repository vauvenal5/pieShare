package org.pieShare.pieShareApp.service;

import java.io.*;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.util.HashMap;
import org.pieShare.pieShareApp.api.IFileService;
import org.pieShare.pieShareApp.api.IFileWatcherService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

public class FileWatcherService implements IFileWatcherService
{

    private final PieLogger logger = new PieLogger(FileWatcherService.class);
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

        //Add all files in this folder to the file list.
        addAllFilesToList();

        watchDir.toPath().register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);	// Register the directory

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
                PieFile pieFile = new PieFile(changedFile);

                if (changedFile.isDirectory() && kind == ENTRY_CREATE)
                {
                    fileService.registerAll(changedFile);
                }

                if (kind == ENTRY_CREATE)
                {
                    System.gc();
                    addNewFile(pieFile);
                }
                else if (kind == ENTRY_MODIFY)
                {
                    System.gc();
                    //fileModified(pieFile);
                }
                else if (kind == ENTRY_DELETE)
                {
                    System.gc();
                    fileDeleted(pieFile);
                }
            }
            boolean valid = key.reset();
            if (!valid)
            {
                break;	// Exit if directory is deleted
            }
        }
    }

    private void addAllFilesToList()
    {
        for (File f : watchDir.listFiles())
        {
            if (f.isFile() || f.isDirectory())
            {
                PieFile pieFile = new PieFile(f);
                addNewFile(pieFile);
            }
        }
    }

    private void addNewFile(PieFile newFile)
    {
        if (files.containsKey(newFile.getRelativeFilePath()))
        {
            if (files.get(newFile.getRelativeFilePath()).equals(newFile))
            {
                logger.debug("Added file is alredy in the list");
            }
        }
        logger.debug("File added to list: " + newFile.getFile().getPath());
        files.put(newFile.getRelativeFilePath(), newFile);
        //Inform Cloud About new File or Folder
    }

    public void fileDeleted(PieFile localFile)
    {
        if (files.containsKey(localFile.getRelativeFilePath()))
        {
            logger.debug("File deleted from list: " + localFile.getFile().getPath());
            
			fileService.folderRemoved(localFile);
			
			files.remove(localFile.getRelativeFilePath());
            //Inform Cloud About File or Folder Delete
        }
        else
        {
            //Deleted Directory .. Or Conflict
        }
    }

    public void fileModified(PieFile localFile)
    {
        if (files.containsKey(localFile.getRelativeFilePath()))
        {
            if (!localFile.getFile().exists())
            {
                fileDeleted(localFile);
            }
            else
            {
                files.remove(localFile.getRelativeFilePath());
                files.put(localFile.getRelativeFilePath(), localFile);
            }
        }
        else
        {
            //File does no Exist, how can that be? Strange ...
        }
    }
	
	@Override
	public void deleteAll()
	{
		for(PieFile file : files.values())
		{
			fileService.folderRemoved(file);
		}
		files.clear();
        System.gc();
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
