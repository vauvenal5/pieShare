/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieTools.pieUtilities.service.hashService.MD5Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author richy
 */
public class PieFile
{

    private File file = null;
    private String md5 = "";
    private String relativeFilePath;

    private IPieShareAppConfiguration pieAppConfig;

    public PieFile()
    {
        
    }

    public void Init(File file)
    {
        this.file = file;

        if (file.exists() && !file.isDirectory())
        {
            try
            {
                md5 = MD5Service.MD5(file);
            }
            catch (IOException ex)
            {
                //ToDo: Error Handling
            }
        }

        Path pathBase = pieAppConfig.getWorkingDirectory().toPath();//new File(pieAppConfig.getWorkingDirectory().getAbsolutePath()).toPath();
        Path pathAbsolute = file.toPath(); // Paths.get("/var/data/stuff/xyz.dat");
        Path pathRelative = pathBase.relativize(pathAbsolute);
        this.relativeFilePath = pathRelative.toString();
    }

    @Autowired
    @Qualifier("pieShareAppConfiguration")
    public void setPieShareAppConfiguration(IPieShareAppConfiguration pieShareAppConfiguration)
    {
        this.pieAppConfig = pieShareAppConfiguration;
    }

    public String getFileName()
    {
        return file.getName();
    }

    public File getFile()
    {
        return file;
    }

    public String getMD5()
    {
        return md5;
    }

    public long getLastModified()
    {
        return file.lastModified();
    }

    public String getRelativeFilePath()
    {
        return relativeFilePath;
        /*
         File workingDir = pieAppConfig.getWorkingDirectory();

         Path pathBase = new File(workingDir.getAbsolutePath()).toPath(); // Paths.get("/var/data");

         Path pathAbsolute = file.toPath(); // Paths.get("/var/data/stuff/xyz.dat");

         Path pathRelative = pathBase.relativize(pathAbsolute);
         return pathRelative.toString();
         */
    }

    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof PieFile))
        {
            return false;
        }

        PieFile pieFile = (PieFile) object;

        if (!pieFile.getRelativeFilePath().equals(this.getRelativeFilePath()))
        {
            return false;
        }

        if (pieFile.getLastModified() != this.getLastModified())
        {
            return false;
        }

        if (!pieFile.getMD5().equals(this.getMD5()))
        {
            return false;
        }

        return true;
    }

}
