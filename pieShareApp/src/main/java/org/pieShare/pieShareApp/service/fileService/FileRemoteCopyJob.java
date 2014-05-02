/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.DataFormatException;
import org.pieShare.pieShareApp.model.message.FileTransferMessageBlocked;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.fileService.api.IFileRemoteCopyJob;
import org.pieShare.pieTools.pieUtilities.service.compressor.api.ICompressor;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author richy
 */
public class FileRemoteCopyJob implements IFileRemoteCopyJob
{

    private PieLogger logger = new PieLogger(FileRemoteCopyJob.class);
    private String relativeFilePath;
    private int actualBlockNumber;
    private HashMap<Integer, File> cachedBlocks;
    private FileOutputStream outStream;
    private File blockDir;
    private IPieShareAppConfiguration pieAppConfig;
    private String fileName;
    private ICompressor compressor;
    private int lastBlockNumber = Integer.MAX_VALUE;
    private boolean isInitialized = false;

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
    }

    private void init(FileTransferMessageBlocked msg)
    {
        actualBlockNumber = 0;
        cachedBlocks = new HashMap<>();

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

        File fileToWrite = new File(blockDir, fileName);

        try
        {
            fileToWrite.createNewFile();
            outStream = new FileOutputStream(fileToWrite);
        }
        catch (FileNotFoundException ex)
        {
            //ToDo Handle Error
        }
        catch (IOException ex)
        {
            //ToDo Handle Error
        }

        isInitialized = true;

    }

    @Override
    public synchronized void newDataArrived(FileTransferMessageBlocked msg) throws IOException, DataFormatException
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
           //ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            
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
            if (cachedBlocks.get(actualBlockNumber).delete())
            {
                logger.error("Cannot delete file part. Part Nr: " + actualBlockNumber);
            }

            cachedBlocks.remove(actualBlockNumber);
            actualBlockNumber++;
        }

        if (actualBlockNumber == lastBlockNumber)
        {
            String a = "";
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
}
