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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import org.pieShare.pieShareApp.model.FileTransferMessageBlocked;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieTools.pieUtilities.service.compressor.api.ICompressor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import sun.audio.AudioDataStream;

/**
 *
 * @author richy
 */
public class FileRemoteCopyJob
{

    private String relativeFilePath;
    private int actualBlockNumber;
    private HashMap<Integer, File> cachedBlocks;
    private FileOutputStream outStream;
    private File blockDir;
    private IPieShareAppConfiguration pieAppConfig;
    private String fileName;
    private ICompressor compressor;
    private int lastBlockNumber = Integer.MAX_VALUE;

    @Autowired
    @Qualifier("compressor")
    public void setCompressor(ICompressor compresor)
    {
        this.compressor = compresor;
    }

    @Autowired
    @Qualifier("pieShareAppConfiguration")
    public void setPieShareAppConfiguration(IPieShareAppConfiguration pieShareAppConfiguration)
    {
        this.pieAppConfig = pieShareAppConfiguration;
    }

    public FileRemoteCopyJob(FileTransferMessageBlocked msg)
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

        File fileToWrite = new File(blokDirName, fileName);

        try
        {
            fileToWrite.createNewFile();
            outStream = new FileOutputStream(fileToWrite);
        }
        catch (FileNotFoundException ex)
        {

        }
        catch (IOException ex)
        {
            
        }

    }

    public void newDataArrived(FileTransferMessageBlocked msg) throws IOException, DataFormatException
    {
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(msg.getBlock());

        compressor.decompressStream(byteInStream, byteOutStream);

        if (msg.isIsLastEmptyMessage())
        {
            lastBlockNumber = msg.getBlockNumber();
        }
        else
        {
            if (msg.getBlockNumber() == actualBlockNumber)
            {
                outStream.write(byteOutStream.toByteArray());
                actualBlockNumber++;
            }
            else
            {
                File cachedFile = new File(blockDir, fileName + "_Part_" + msg.getBlockNumber());
                cachedFile.createNewFile();
                FileOutputStream ff = new FileOutputStream(cachedFile);
                ff.write(byteOutStream.toByteArray());
                cachedBlocks.put(msg.getBlockNumber(), cachedFile);
            }

            while (cachedBlocks.containsKey(actualBlockNumber))
            {
                FileInputStream ff = new FileInputStream(cachedBlocks.get(actualBlockNumber));
                byte[] block = new byte[1024];

                int readBytes = 0;
                while ((readBytes = ff.read(block)) != -1)
                {
                    outStream.write(block, 0, readBytes);
                }
                actualBlockNumber++;
            }
        }
        if (actualBlockNumber == lastBlockNumber)
        {
            //Ende
        }

    }

}
