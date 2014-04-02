/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.HashMap;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.pieShare.pieShareApp.api.IFileService;
import org.pieShare.pieShareApp.api.IFileWatcherService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class ApacheFileWatcher implements IFileWatcherService
{

	private PieLogger logger = new PieLogger(ApacheFileWatcher.class);
	private IFileService fileService;
	private File watchDir;
	private HashMap<String, PieFile> files;

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
		files = new HashMap<>();
		
		FileSystemManager fileSystemManager = VFS.getManager();
		FileObject dirToWatchFO = null;
		dirToWatchFO = fileSystemManager.resolveFile(watchDir.getAbsolutePath());

		DefaultFileMonitor fileMonitor = new DefaultFileMonitor(new FileListener()
		{

			@Override
			public void fileCreated(FileChangeEvent fce) throws Exception
			{
				
				fce.getFile().
			}

			@Override
			public void fileDeleted(FileChangeEvent fce) throws Exception
			{
				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}

			@Override
			public void fileChanged(FileChangeEvent fce) throws Exception
			{
				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}
		});

		fileMonitor.setRecursive(false);
		fileMonitor.addFile(dirToWatchFO);
		fileMonitor.start();

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
		}
	}

}
