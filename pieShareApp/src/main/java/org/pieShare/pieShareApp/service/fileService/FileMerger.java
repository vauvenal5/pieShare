/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import org.apache.commons.lang3.Validate;
import org.pieShare.pieShareApp.model.FileChangedMessage;
import org.pieShare.pieShareApp.service.configurationService.PieShareAppConfiguration;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.fileService.api.IFileMerger;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Richard
 */
public class FileMerger implements IFileMerger
{

    private final PieLogger logger = new PieLogger(FileMerger.class);
    private IFileService fileService;
    private HashMap<String, PieDirectory> dirs;
    private IPieShareAppConfiguration pieAppConfig;
    private IBeanService beanService;

    public FileMerger()
    {
        dirs = new HashMap<>();
    }

    public void setBeanService(IBeanService beanService)
    {
        this.beanService = beanService;
    }

    @Autowired
    @Qualifier("pieShareAppConfiguration")
    public void setPieShareAppConfiguration(IPieShareAppConfiguration pieShareAppConfiguration)
    {
        this.pieAppConfig = pieShareAppConfiguration;
    }

    @Override
    public void setFileService(IFileService fileService)
    {
        this.fileService = fileService;
    }

    @Override
    public void fileCreated(File file) throws BeanServiceException
    {
        if (!file.exists())
        {
            //ToDo: It must not exist, because it can be from an remote location. But check this when we insert remote merger.
            logger.debug("Create File: File does not exist, maybe from remote location.");
            //return;
        }

        if (file.isDirectory())
        {
            PieDirectory dir;
            dir = beanService.getBean(PieDirectory.class);
            dir.init(file);

            if (!dirs.containsKey(dir.getRelativeFilePath()))
            {
                dirs.put(dir.getRelativeFilePath(), dir);
            }
        }
        else
        {
            PieFile pieFile = beanService.getBean(PieFile.class);
            pieFile.Init(file);

            PieDirectory dir = beanService.getBean(PieDirectory.class);
            dir.init(pieFile.getFile().getParentFile());

            if (!dirs.containsKey(dir.getRelativeFilePath()))
            {
                dirs.put(dir.getRelativeFilePath(), dir);
            }

            checkListForNewFile(dirs.get(dir.getRelativeFilePath()).getFiles(), pieFile);
        }
    }

    @Override
    public void fileDeleted(File file) throws BeanServiceException
    {
        PieFile pieFile = beanService.getBean(PieFile.class);
        pieFile.Init(file);

        //If file is in Direcotry list it is an Dir, so we delete it and OK
        if (dirs.containsKey(pieFile.getRelativeFilePath()))
        {
            PieDirectory dir = dirs.get(pieFile.getRelativeFilePath());
            for (PieFile f : dir.getFiles().values())
            {
                sendNewMessage(FileChangedTypes.FILE_DELETED, f);
            }

            dirs.remove(pieFile.getRelativeFilePath());
        }
        else
        {
            //If not in  dir list it is a file, get parent folder.
            PieDirectory dir = beanService.getBean(PieDirectory.class);
            dir.init(file.getParentFile());

            if (!dirs.containsKey(dir.getRelativeFilePath()))
            {
                logger.debug("File Delete: Cannot find folder from file to delete, is alredy delted!");
                return;
            }

            deleteFileFromList(dirs.get(dir.getRelativeFilePath()).getFiles(), pieFile);

            if (dirs.get(dir.getRelativeFilePath()).getFiles().isEmpty())
            {
                dirs.remove(dir.getRelativeFilePath());
            }
        }
    }

    @Override
    public void fileChanged(File file) throws BeanServiceException
    {
        PieFile pieFile = beanService.getBean(PieFile.class);
        pieFile.Init(file);

        if (dirs.containsKey(pieFile.getRelativeFilePath()))
        {
            logger.debug("ChangedFile: Changed File is a Folder. Do nothing!!. ");
            //Changed File is a Folder. 
            //Ignore
            return;
        }

        PieDirectory dir = beanService.getBean(PieDirectory.class);
        dir.init(file.getParentFile());

        if (!dirs.containsKey(dir.getRelativeFilePath()))
        {
            //The folder from changed file is not in the folder list. Error.
            logger.debug("File Changed: The folder from changed file is not in the folder list, add foder to list!!.");

            dirs.put(dir.getRelativeFilePath(), dir);

            return;
        }

        checkListForChangedFile(dirs.get(dir.getRelativeFilePath()).getFiles(), pieFile);
    }

    private void checkListForChangedFile(HashMap<String, PieFile> files, PieFile pieFile) throws BeanServiceException
    {
        if (files.containsKey(pieFile.getRelativeFilePath()))
        {
            if (files.get(pieFile.getRelativeFilePath()).equals(pieFile))
            {
                logger.debug("Changed File: File is exactly same. Do not Change.");
                //Is Exectly same file, do nothing
                return;
            }

            logger.debug("Changed File: Remove old and add new file");

            files.remove(pieFile.getRelativeFilePath());
            files.put(pieFile.getRelativeFilePath(), pieFile);

            sendNewMessage(FileChangedTypes.FILE_MODIFIED, pieFile);
        }
        else
        {
            logger.debug("Changed File: File does not exist call fileCreated()");
            fileCreated(pieFile.getFile());
        }
    }

    private void checkListForNewFile(HashMap<String, PieFile> files, PieFile pieFile) throws BeanServiceException
    {
        if (files.containsKey(pieFile.getRelativeFilePath()))
        {
            logger.debug("Created File: File is alrey in list, call fileChanged().");
            checkListForChangedFile(files, pieFile);
        }
        else
        {
            logger.debug("Created File: Add new created file");
            files.put(pieFile.getRelativeFilePath(), pieFile);
            sendNewMessage(FileChangedTypes.FILE_CREATED, pieFile);
        }
    }

    private void deleteFileFromList(HashMap<String, PieFile> files, PieFile pieFile) throws BeanServiceException
    {
        if (files.containsKey(pieFile.getRelativeFilePath()))
        {
            if (files.get(pieFile.getRelativeFilePath()).equals(pieFile))
            {
                logger.debug("Deleted File: Delete file from list");
                files.remove(pieFile.getRelativeFilePath());
                sendNewMessage(FileChangedTypes.FILE_DELETED, pieFile);

            }
            else
            {
                logger.debug("Deleted File: Files are not equal, do not remove file");
            }
        }
        else
        {
            logger.debug("Deleted File: File to delete does not exist.");
        }
    }

    private void sendNewMessage(FileChangedTypes type, PieFile file) throws BeanServiceException
    {
        FileChangedMessage msg = beanService.getBean(FileChangedMessage.class);
        msg.setChangedType(type);
        msg.setLastModified(file.getLastModified());
        msg.setMd5(file.getMD5());
        msg.setRelativeFilePath(file.getRelativeFilePath());

        logger.debug("Send Message: Send " + type.toString() + " event. FileName: " + file.getFile().getName());

        fileService.localFileChange(msg);
    }

    @Override
    public void remoteFileChanged(FileChangedMessage fileChangedMessage) throws BeanServiceException
    {
        logger.debug("Remote File Changed: Remote file has changed. Check if needed.");
        if (dirs.containsKey(fileChangedMessage.getRelativeFilePath()))
        {
            logger.debug("Remote File Changed: File is Dir. Do nothing.");
            return;
        }

        File file = new File(pieAppConfig.getWorkingDirectory(), fileChangedMessage.getRelativeFilePath());

        PieFile createdFile = beanService.getBean(PieFile.class);
        createdFile.Init(file);

        PieDirectory dir = beanService.getBean(PieDirectory.class);
        dir.init(file.getParentFile());

        if (dirs.containsKey(dir.getRelativeFilePath()))
        {
            if (dirs.get(dir.getRelativeFilePath()).getFiles().containsKey(createdFile.getRelativeFilePath()))
            {
                PieFile localFile = dirs.get(dir.getRelativeFilePath()).getFiles().get(createdFile.getRelativeFilePath());

                if (localFile.getLastModified() < fileChangedMessage.getLastModified())
                {
                    fileChanged(file);
                    //Copy This Shit
                }
                else
                {
                    //Local File is newer than remote file, so do nothing:
                    //ToDo: Check this when implementing an better file merger for remote file.
                    logger.debug("Remote File Changed: Remote file is older then local. Do nothing.");
                }

            }
            else
            {
                fileCreated(file);
            }
        }
        else
        {
            fileCreated(file);
        }

    }

    @Override
    public HashMap<String, PieDirectory> getDirs()
    {
        return dirs;
    }

    @Override
    public PieFile getFile(String relativeFilePath) throws BeanServiceException, FileNotFoundException
    {
        Validate.notNull(relativeFilePath);
        Validate.notEmpty(relativeFilePath);
        
        File file = new File(pieAppConfig.getWorkingDirectory(), relativeFilePath);
        PieDirectory dir = beanService.getBean(PieDirectory.class);
        dir.init(file.getParentFile());

        if (dirs.containsKey(dir.getRelativeFilePath()))
        {
            if(dirs.get(dir.getRelativeFilePath()).getFiles().containsKey(relativeFilePath))
            {
                logger.debug("File: " + relativeFilePath + " found. Returning PieFile");
                return dirs.get(dir.getRelativeFilePath()).getFiles().get(relativeFilePath);
            }
        }
        logger.error("File: " + relativeFilePath + " not in file list");
        throw new FileNotFoundException("File: " + relativeFilePath + " not in file list");
    }
}
