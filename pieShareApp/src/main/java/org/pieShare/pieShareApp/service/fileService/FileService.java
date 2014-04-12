package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.Validate;
import org.pieShare.pieShareApp.model.AllFilesSyncMessage;
import org.pieShare.pieShareApp.model.FileChangedMessage;
import org.pieShare.pieShareApp.model.task.AllFilesSyncTask;
import org.pieShare.pieShareApp.model.task.FileChangedTask;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.fileService.api.IFileMerger;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.api.IFileWatcherService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author richy
 */
public class FileService implements IFileService
{

    private final PieLogger logger = new PieLogger(FileService.class);
    private IClusterService clusterService;
    private IFileMerger fileMerger = null;
    private IExecutorService executorService = null;
    private IFileWatcherService fileWatcher;
    private ArrayList<UUID> pendingTasks;
    private IPieShareAppConfiguration pieAppConfig;

    public FileService()
    {
    }

    @Autowired
    @Qualifier("pieShareAppConfiguration")
    public void setPieShareAppConfiguration(IPieShareAppConfiguration pieShareAppConfiguration)
    {
        this.pieAppConfig = pieShareAppConfiguration;
    }

    @PostConstruct
    public void initFileService()
    {
        pendingTasks = new ArrayList<>();

        try
        {
            registerAll(pieAppConfig.getWorkingDirectory());

        }
        catch (IOException ex)
        {
            logger.error("Error parsing workingDir at startup");
        }

        addWatchDirectory(pieAppConfig.getWorkingDirectory());
    }

    public void setClusterService(IClusterService clusterService)
    {
        this.clusterService = clusterService;
    }

    public void setFileWatcher(IFileWatcherService fileWatcher)
    {
        this.fileWatcher = fileWatcher;
    }

    public void setExecutorService(IExecutorService executorService)
    {
        this.executorService = executorService;
        this.executorService.registerTask(FileChangedMessage.class, FileChangedTask.class);
        this.executorService.registerTask(AllFilesSyncMessage.class, AllFilesSyncTask.class);
    }

    public void setFileMerger(IFileMerger fileMerger)
    {
        this.fileMerger = fileMerger;
        fileMerger.setFileService(this);
    }

    private void addWatchDirectory(File file)
    {
        fileWatcher.setWatchDir(file);
        executorService.execute(fileWatcher);
    }

    private void registerAll(File file) throws IOException
    {
        Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                try
                {
                    fileMerger.fileCreated(file.toFile());
                }
                catch (BeanServiceException ex)
                {
                    logger.error("Error adding File at startup register");
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
                try
                {
                    fileMerger.fileCreated(dir.toFile());
                }
                catch (BeanServiceException ex)
                {
                    logger.error("Error adding File at startup register");
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public void remoteFileChange(FileChangedMessage message)
    {
        if (message == null)
        {
            logger.debug("File Service: Recieved null FileChangedMessage");
            return;
        }

        File wdir = pieAppConfig.getWorkingDirectory();
        File newFile = new File(wdir, message.getRelativeFilePath());

        try
        {
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
        catch (BeanServiceException ex)
        {
            logger.error("Error adding remote file. Mesage: " + ex.getMessage());
        }
    }

    @Override
    public void localFileChange(FileChangedMessage message)
    {
        Validate.notNull(message);

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
    public void remoteAllFilesSyncRequest(AllFilesSyncMessage msg)
    {
        if (msg == null)
        {
            logger.debug("File Service: Recieved null AllFilesSyncMessage");
            return;
        }

        if (msg.isIsRequest())
        {
            logger.debug("File Service: New AllFilesMessage request arrvied");

            if (msg.getId() == null)
            {
                logger.debug("File Service: Arrived AllFilesMessage request had no ID. Return.");
                return;
            }

            AllFilesSyncMessage sendMsg = new AllFilesSyncMessage();
            sendMsg.setId(msg.getId());
            sendMsg.setIsRequest(false);

            for (PieDirectory dir : fileMerger.getDirs().values())
            {
                for (PieFile file : dir.getFiles().values())
                {
                    FileChangedMessage fileChangedMessage = new FileChangedMessage();
                    fileChangedMessage.setChangedType(FileChangedTypes.FILE_MODIFIED);
                    fileChangedMessage.setLastModified(file.getLastModified());
                    fileChangedMessage.setMd5(file.getMD5());
                    fileChangedMessage.setRelativeFilePath(file.getRelativeFilePath());
                    sendMsg.getList().add(fileChangedMessage);
                }
            }

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

            if (msg.getId() == null)
            {
                //ToDo: When id not null, but dirs, check if id is in pending tasks.
                logger.debug("File Service: Error in Message: No ID. Return");
                return;
            }

            if (!pendingTasks.contains(msg.getId()))
            {
                return;
            }

            pendingTasks.remove(msg.getId());

            for (FileChangedMessage fileChangedMsg : msg.getList())
            {
                try
                {
                    //File newFile = new File(Configuration.getWorkingDirectory(), fileChangedMsg.getRelativeFilePath());
                    fileMerger.remoteFileChanged(fileChangedMsg);
                }
                catch (BeanServiceException ex)
                {
                    logger.error("Error calling function remoteFileChanged in FileMerger. Message: " + ex.getMessage());
                }
            }
        }
    }

    @Override
    public void sendAllFilesSyncRequest()
    {
        logger.debug("File Service: Send new AllFilesRequest");
        UUID id = UUID.randomUUID();

        AllFilesSyncMessage msg = new AllFilesSyncMessage();
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
