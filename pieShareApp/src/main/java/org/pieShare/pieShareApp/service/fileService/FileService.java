package org.pieShare.pieShareApp.service.fileService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.Validate;
import org.pieShare.pieShareApp.model.AllFilesSyncMessage;
import org.pieShare.pieShareApp.model.FileChangedMessage;
import org.pieShare.pieShareApp.model.FileTransferMessageBlocked;
import org.pieShare.pieShareApp.model.FileTransferRequestMessage;
import org.pieShare.pieShareApp.model.task.AllFilesSyncTask;
import org.pieShare.pieShareApp.model.task.FileChangedTask;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.fileService.api.IFileMerger;
import org.pieShare.pieShareApp.service.fileService.api.IFileRemoteCopyJob;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.api.IFileWatcherService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.compressor.api.ICompressor;
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
    private IBeanService beanService;
    private ICompressor compressor;
    private HashMap<UUID, IFileRemoteCopyJob> fileCopyJobs;

    public FileService()
    {

    }

    @Autowired
    @Qualifier("pieShareAppConfiguration")
    public void setPieShareAppConfiguration(IPieShareAppConfiguration pieShareAppConfiguration)
    {
        this.pieAppConfig = pieShareAppConfiguration;
    }

    @Autowired
    @Qualifier("compressor")
    public void setCompressor(ICompressor compresor)
    {
        this.compressor = compresor;
    }

    public void setBeanService(IBeanService beanService)
    {
        this.beanService = beanService;
    }

    @PostConstruct
    public void initFileService()
    {
        pendingTasks = new ArrayList<>();
        fileCopyJobs = new HashMap<>();

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
    public synchronized void remoteFileChange(FileChangedMessage message)
    {
        if (message == null)
        {
            logger.debug("File Service: Recieved null FileChangedMessage");
            return;
        }

        //File wdir = pieAppConfig.getWorkingDirectory();
        //File newFile = new File(wdir, message.getRelativeFilePath());
        
        try
        {
            fileMerger.remoteFileChanged(message);

        }
        catch (BeanServiceException ex)
        {
            logger.error("Error adding remote file. Mesage: " + ex.getMessage());
        }

        /*        try
         {
         if (message.getChangedType() == FileChangedTypes.FILE_CREATED)
            
            
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
         }*/
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
                    fileChangedMessage.setChangedType(FileChangedTypes.SNYC_ALL);
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

    @Override
    public void fileTransferRequestReceived(FileTransferRequestMessage msg)
    {
        Validate.notNull(msg);

        PieFile file = null;

        try
        {
            file = fileMerger.getFile(msg.getRelativeFilePath());
        }
        catch (BeanServiceException | FileNotFoundException ex)
        {
            logger.error("Error reading file from merger. Message: " + ex.getMessage());
            return;
        }

        int buffSize = pieAppConfig.getFileSendBufferSize();
        byte[] sendBuffer = new byte[buffSize];

        FileInputStream fileStream = null;
        try
        {
            fileStream = new FileInputStream(file.getFile());
        }
        catch (FileNotFoundException ex)
        {
            logger.error("Requested file is not avalible ob HDD. Message: " + ex.getMessage());
            return;
        }

        int readBytes = 0;
        int count = 0;

        try
        {
            while ((readBytes = fileStream.read(sendBuffer)) != -1)
            {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                ByteArrayInputStream inStream = new ByteArrayInputStream(sendBuffer, 0, readBytes);
                compressor.compressStream(inStream, outStream);

                FileTransferMessageBlocked sendMessage = new FileTransferMessageBlocked();
                sendMessage.setId(msg.getId());
                sendMessage.setIsLastEmptyMessage(false);
                sendMessage.setBlockNumber(count++);
                sendMessage.setBlock(outStream.toByteArray());
                sendMessage.setRelativeFilePath(file.getRelativeFilePath());
                clusterService.sendMessage(sendMessage);
            }

            FileTransferMessageBlocked sendMessage = new FileTransferMessageBlocked();
            sendMessage.setIsLastEmptyMessage(true);
            sendMessage.setId(msg.getId());
            sendMessage.setRelativeFilePath(file.getRelativeFilePath());
            sendMessage.setBlockNumber(count);
            clusterService.sendMessage(sendMessage);
        }
        catch (IOException ex)
        {
            logger.error("Error in Comressor Service. Message: " + ex.getMessage());
        }
        catch (ClusterServiceException ex)
        {
            logger.error("Error in Cluster Service. Message: " + ex.getMessage());
        }

    }

    @Override
    public void fileTransfereMessage(FileTransferMessageBlocked msg)
    {
        Validate.notNull(msg);
        Validate.notNull(msg.getId());

        IFileRemoteCopyJob fileRemoteCopyJob = null;

        if (pendingTasks.contains(msg.getId()))
        {
            logger.debug("File Trasfere Message Recieved. Task id not avalible in task list. Return.");

            try
            {
                fileRemoteCopyJob = beanService.getBean(FileRemoteCopyJob.class);
                fileCopyJobs.put(msg.getId(), fileRemoteCopyJob);
            }
            catch (BeanServiceException ex)
            {
                logger.debug("Error getting new fileRemoteCopyJob from beanService. Message: " + ex.getMessage());
                return;
            }
        }
        else
        {
            if (fileCopyJobs.containsKey(msg.getId()))
            {
                fileRemoteCopyJob = fileCopyJobs.get(msg.getId());
            }
            else
            {
                logger.debug("No fileCopyJob for this ID in list. Return:");
                return;
            }
        }

        try
        {
            fileRemoteCopyJob.newDataArrived(msg);
        }
        catch (IOException ex)
        {
            //Handle File Lists
            logger.debug("IO Error in fileCopyJob. Message: " + ex.getMessage());

            if (fileCopyJobs.containsKey(msg.getId()))
            {
                fileCopyJobs.get(msg.getId()).cleanUP();
                fileCopyJobs.remove(msg.getId());
            }
            return;
        }
        catch (DataFormatException ex)
        {
            //Handle File Lists
            logger.debug("Error Compressor Message: " + ex.getMessage());

            if (fileCopyJobs.containsKey(msg.getId()))
            {
                fileCopyJobs.get(msg.getId()).cleanUP();
                fileCopyJobs.remove(msg.getId());
            }
            return;
        }

    }

    @Override
    public void sendFileTransferRequenst(PieFile piefile)
    {
        FileTransferRequestMessage requestMsg = null;
        try
        {
            requestMsg = beanService.getBean(FileTransferRequestMessage.class);
        }
        catch (BeanServiceException ex)
        {
            //ToDo: Handle file lists.
            logger.error("Error getting new FileTransferRewuestMessage from beans. Message: " + ex.getMessage());
            return;
        }

        UUID id = UUID.randomUUID();
        requestMsg.setId(id);
        pendingTasks.add(id);
        requestMsg.setRelativeFilePath(piefile.getRelativeFilePath());

        try
        {
            clusterService.sendMessage(requestMsg);
        }
        catch (ClusterServiceException ex)
        {
            //ToDo: Handle file lists.
            logger.error("Error sending FileTransferMessage. Message: " + ex.getMessage());
        }
    }

}
