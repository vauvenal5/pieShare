package org.pieShare.pieShareApp.service.fileService;

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
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.FileTransferMessageBlocked;
import org.pieShare.pieShareApp.model.message.FileTransferRequestMessage;
import org.pieShare.pieShareApp.model.message.AllFilesSyncMessage;
import org.pieShare.pieShareApp.model.message.FileChangedMessage;
import org.pieShare.pieShareApp.model.task.AllFilesSyncTask;
import org.pieShare.pieShareApp.model.task.FileChangedTask;
import org.pieShare.pieShareApp.model.task.FileTransferRequestTask;
import org.pieShare.pieShareApp.model.task.FileTransferTask;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.fileService.api.IFileMerger;
import org.pieShare.pieShareApp.service.fileService.api.IFileRemoteCopyJob;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.api.IFileWatcherService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.compressor.api.ICompressor;
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
    private IFileWatcherService fileWatcher;
    private ArrayList<UUID> pendingTasks;
    private IPieShareAppConfiguration pieAppConfig;
    private IBeanService beanService;
    private ICompressor compressor;
    private HashMap<UUID, IFileRemoteCopyJob> fileCopyJobs;
    private IClusterManagementService clusterManagementService;

    public FileService()
    {

    }

    public void setPieShareAppConfiguration(IPieShareAppConfiguration pieShareAppConfiguration)
    {
        this.pieAppConfig = pieShareAppConfiguration;
    }

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
            PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());

            Validate.notNull(user.getCloudName());
            //ToDo: Handle when user cloud name = null;

            this.clusterService = clusterManagementService.connect(user.getCloudName());
        }
        catch (ClusterManagmentServiceException ex)
        {
            //ToDo Handle Error
        }

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

    public void setClusterManagementService(IClusterManagementService clusterManagementService)
    {
        this.clusterManagementService = clusterManagementService;
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
        this.executorService.registerTask(FileTransferRequestMessage.class, FileTransferRequestTask.class);
        this.executorService.registerTask(FileTransferMessageBlocked.class, FileTransferTask.class);
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
    public synchronized void remoteFileChange(FileChangedMessage message)
    {
        if (message == null)
        {
            logger.debug("File Service: Recieved null FileChangedMessage");
            return;
        }

        fileMerger.remoteFileChanged(message);
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
                    //fileChangedMessage.setMd5(file.getMD5());
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
                //File newFile = new File(Configuration.getWorkingDirectory(), fileChangedMsg.getRelativeFilePath());
                fileMerger.remoteFileChanged(fileChangedMsg);
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
        catch (FileNotFoundException ex)
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
            logger.error("Requested file is not avalible on HDD. Message: " + ex.getMessage());
            return;
        }

        int readBytes = 0;
        int count = 0;

        try
        {
            while ((readBytes = fileStream.read(sendBuffer)) != -1)
            {
                byte[] temp = new byte[readBytes];
                System.arraycopy(sendBuffer, 0, temp, 0, readBytes);

                byte[] sendArr = compressor.compressByteArray(temp, readBytes);

                //ToDo: Get from beand (Do not forget prototype)
                FileTransferMessageBlocked sendMessage = new FileTransferMessageBlocked();
                sendMessage.setId(msg.getId());
                sendMessage.setIsLastEmptyMessage(false);
                sendMessage.setBlockNumber(count);
                sendMessage.setBlock(sendArr);
                sendMessage.setRelativeFilePath(file.getRelativeFilePath());
                sendMessage.setAddress(msg.getAddress());
                sendMessage.setBlockSize(sendArr.length);
               
                
                clusterService.sendMessage(sendMessage);
                logger.error("FileService: Sent Number: " + count);
                count++;
            }

            FileTransferMessageBlocked sendMessage = new FileTransferMessageBlocked();
            sendMessage.setIsLastEmptyMessage(true);
            sendMessage.setId(msg.getId());
            sendMessage.setRelativeFilePath(file.getRelativeFilePath());
            sendMessage.setBlockNumber(count);
            sendMessage.setAddress(msg.getAddress());
            
            clusterService.sendMessage(sendMessage);
            logger.error("FileService: Sending Complete. Sent Number: " + count);
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

        synchronized (this)
        {
            if (pendingTasks.contains(msg.getId()))
            {
                logger.debug("File Trasfere Message Recieved. Start new FileCopyJob.");

                pendingTasks.remove(msg.getId());

                fileRemoteCopyJob = beanService.getBean(FileRemoteCopyJob.class);
                fileCopyJobs.put(msg.getId(), fileRemoteCopyJob);

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
        }

    }

    @Override
    public void sendFileTransferRequenst(FileTransferRequestMessage requestMsg)
    {
       // FileTransferRequestMessage requestMsg = null;

        //requestMsg = beanService.getBean(FileTransferRequestMessage.class);

        UUID id = UUID.randomUUID();
        requestMsg.setId(id);
        pendingTasks.add(id);
        //requestMsg.setRelativeFilePath(piefile.getRelativeFilePath());

        try
        {
            clusterService.sendMessage(requestMsg);
        }
        catch (ClusterServiceException ex)
        {
            logger.error("Error sending FileTransferRequest. Message: " + ex.getMessage());
        }
        
        
    }

}
