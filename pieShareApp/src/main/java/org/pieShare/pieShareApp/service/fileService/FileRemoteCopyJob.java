/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.DataFormatException;
import org.pieShare.pieShareApp.model.message.FileTransferMessageBlocked;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.fileService.api.IFileRemoteCopyJob;
import org.pieShare.pieShareApp.service.fileService.exceptions.FilePartMissingException;
import org.pieShare.pieTools.pieUtilities.service.compressor.api.ICompressor;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.utils.FileUtils;

/**
 *
 * @author richy
 */
public class FileRemoteCopyJob implements IFileRemoteCopyJob
{

    private PieLogger logger = new PieLogger(FileRemoteCopyJob.class);
    private int actualBlockNumber;
    // List<String> list = Collections.synchronizedList(new ArrayList<String>());
    private final ConcurrentHashMap<Integer, File> cachedBlocks;
    private FileOutputStream outStream;
    private File blockDir;
    private File fileToWrite;
    private IPieShareAppConfiguration pieAppConfig;
    private String fileName;
    private ICompressor compressor;
    private long lastBlockNumber = Long.MAX_VALUE;
    private boolean isInitialized = false;
    private String relativeFilePath;
    long long ff;
    
    public void setCompressor(ICompressor compresor)
    {
        this.compressor = compresor;
    }

    public void setPieShareAppConfiguration(IPieShareAppConfiguration pieShareAppConfiguration)
    {
        this.pieAppConfig = pieShareAppConfiguration;
    }

    public FileRemoteCopyJob()
    {
        cachedBlocks = new ConcurrentHashMap<>();
        //cachedBlocks = new HashMap<>();
    }

    private synchronized void init(FileTransferMessageBlocked msg) throws FileNotFoundException, IOException
    {
        if (isInitialized)
        {
            return;
        }

        actualBlockNumber = 0;

        this.relativeFilePath = msg.getRelativeFilePath();

        File f = new File(msg.getRelativeFilePath());
        fileName = f.getName();

        String blokDirName = ".copyJobf_" + fileName;
        blockDir = new File(pieAppConfig.getTempCopyDirectory(), blokDirName);

        boolean found = false;
        int index = 0;
        while (!found)
        {
            if (blockDir.exists())
            {
                blockDir = new File(pieAppConfig.getTempCopyDirectory(), blokDirName + "_" + index++);
            }
            else
            {
                blockDir.mkdirs();
                found = true;
            }
        }

        fileToWrite = new File(blockDir, fileName);

        fileToWrite.createNewFile();
        outStream = new FileOutputStream(fileToWrite);

        isInitialized = true;

    }

    //  @Override
    public synchronized void newDataArrived(FileTransferMessageBlocked msg) throws IOException, DataFormatException, FileNotFoundException
    {
        if (!isInitialized)
        {
            init(msg);
        }

        logger.info("FilePart Recieved: Number :" + msg.getBlockNumber());

        if (msg.isIsLastEmptyMessage())
        {
            lastBlockNumber = msg.getBlockNumber();
        }
        else
        {
            byte[] toWrite = compressor.decompressByteArray(msg.getBlock());//.decompressStream(msg.getBlock(), byteOutStream);

            if (msg.getBlockNumber() == actualBlockNumber)
            {
                //byte[] toWrite = byteOutStream.toByteArray();
                outStream.write(toWrite);
                outStream.flush();
                actualBlockNumber++;
            }
            else
            {
                File cachedFile = new File(blockDir, fileName + "_Part_" + msg.getBlockNumber());
                cachedFile.createNewFile();
                try (FileOutputStream ff = new FileOutputStream(cachedFile))
                {
                    ff.write(toWrite);
                    ff.flush();
                    ff.close();
                }
                cachedBlocks.put(msg.getBlockNumber(), cachedFile);
            }
        }

        while (cachedBlocks.containsKey(actualBlockNumber))
        {
            try (FileInputStream ff = new FileInputStream(cachedBlocks.get(actualBlockNumber)))
            {
                byte[] block = new byte[1024];

                int readBytes = 0;
                while ((readBytes = ff.read(block)) != -1)
                {
                    outStream.write(block, 0, readBytes);
                    outStream.flush();
                }
                ff.close();
            }
            if (!cachedBlocks.get(actualBlockNumber).delete())
            {
                logger.error("Cannot delete file part. Part Nr: " + actualBlockNumber);
            }

            cachedBlocks.remove(actualBlockNumber);
            actualBlockNumber++;
        }

        if (actualBlockNumber == lastBlockNumber)
        {
            outStream.close();

            File newFile = new File(pieAppConfig.getWorkingDirectory(), msg.getRelativeFilePath());

            if (!newFile.getParentFile().exists())
            {
                newFile.getParentFile().mkdirs();
            }

            Files.copy(fileToWrite.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            cleanUP();
        }
    }

    @Override
    public void copyFilePartToTemp(FileTransferMessageBlocked msg) throws IOException, DataFormatException, FilePartMissingException
    {
        
        if (!isInitialized)
        {
            init(msg);
        }

        if (msg.isIsLastEmptyMessage())
        {
            lastBlockNumber = msg.getBlockNumber();
        }
        else
        {
            logger.info("FilePart Recieved: Number :" + msg.getBlockNumber());
            byte[] toWrite = compressor.decompressByteArray(msg.getBlock());
            File cachedFile = new File(blockDir, fileName + "_Part_" + msg.getBlockNumber());
            cachedFile.createNewFile();
            try (FileOutputStream ff = new FileOutputStream(cachedFile))
            {
                ff.write(toWrite);
                ff.flush();
                ff.close();
            }
            cachedBlocks.put(msg.getBlockNumber(), cachedFile);
        }

        if (cachedBlocks.size() == lastBlockNumber - 1)
        {
            buildFileAndCopyToWorkDir();
        }

    }

    @Override
    public void cleanUP()
    {
        if (!blockDir.exists())
        {
            return;
        }

        try
        {
            FileUtils.deleteRecursive(blockDir);
        }
        catch (FileNotFoundException ex)
        {
            logger.debug("Error clean up FileRemoteCopy. Message:" + ex.getMessage());
        }
    }

    private void buildFileAndCopyToWorkDir() throws IOException, FilePartMissingException
    {
        while (cachedBlocks.containsKey(actualBlockNumber))
        {
            try (FileInputStream ff = new FileInputStream(cachedBlocks.get(actualBlockNumber)))
            {
                byte[] block = new byte[1024];

                int readBytes = 0;
                while ((readBytes = ff.read(block)) != -1)
                {
                    outStream.write(block, 0, readBytes);
                    outStream.flush();
                }
                ff.close();
            }

            if (!cachedBlocks.get(actualBlockNumber).delete())
            {
                logger.error("Cannot delete file part. Part Nr: " + actualBlockNumber);
            }

            cachedBlocks.remove(actualBlockNumber);
            actualBlockNumber++;

        }

        if (!cachedBlocks.isEmpty())
        {
            throw new FilePartMissingException("One File party is Missing. Last written Partnumber is: " + actualBlockNumber);
        }

        logger.info("All parts arrived. Copy file to workingDir.");

        outStream.close();

        File newFile = new File(pieAppConfig.getWorkingDirectory(), relativeFilePath);

        if (!newFile.getParentFile().exists())
        {
            newFile.getParentFile().mkdirs();
        }

        Files.copy(fileToWrite.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        cleanUP();

    }
}
