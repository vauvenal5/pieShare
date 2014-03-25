package org.pieShare.pieShareApp.service;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.pieShare.pieShareApp.api.IFileService;
import org.pieShare.pieShareApp.configuration.Configuration;
import org.pieShare.pieShareApp.model.FileChangedMessage;
import org.pieShare.pieShareApp.model.task.FileChangedTask;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;

/**
 *
 * @author richy
 */
public class FileService implements IFileService
{
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final PieLogger logger = new PieLogger(FileService.class);
    private IClusterService clusterService;
    private FileChangedTask fileChangedTask = null;
    private HashMap<String, PieFile> files = null;

    public FileService()
    {
        fileChangedTask = new FileChangedTask();
        fileChangedTask.setFileService(this);
        files = new HashMap<String, PieFile>();
        
        newFolderAdded(Configuration.getWorkingDirectory());
        
    }

    public void serClusterService(IClusterService clusterService)
    {
        this.clusterService = clusterService;
        clusterService.registerTask(FileChangedMessage.class, fileChangedTask);
    }

    
    @Override
    public void newFolderAdded(File folder)
    {
        FileWatcherService service = new FileWatcherService();
        service.setFileService(this);
        service.setWatchDir(folder);
        executor.execute(new FileWatcherService());
    }
    
    
    @Override
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
    public void localFileAdded(PieFile localFile)
    {
        if (files.containsKey(localFile.getRelativeFilePath()))
        {
           if(files.get(localFile.getRelativeFilePath()).equals(localFile))
           {
               logger.debug("Added file is alredy in the list");
           }
        }

        files.put(localFile.getRelativeFilePath(), localFile);
    }

    @Override
    public void localFileModified(PieFile localFile)
    {
        if (files.containsKey(localFile.getRelativeFilePath()))
        {
            files.remove(localFile.getRelativeFilePath());
            files.put(localFile.getRelativeFilePath(), localFile);
        }
        else
        {
            //File does no Exist, how can that be? Strange ...
        }
    }

    @Override
    public void localFileDeleted(PieFile localFile)
    {
        if (files.containsKey(localFile.getRelativeFilePath()))
        {
            files.remove(localFile.getRelativeFilePath());
        }
    }
}
