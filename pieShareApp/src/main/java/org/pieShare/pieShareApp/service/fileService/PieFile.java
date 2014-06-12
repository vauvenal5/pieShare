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
import org.pieShare.pieTools.pieUtilities.service.security.hashService.MD5Service;
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
    private long lastModified;
    private IPieShareAppConfiguration pieAppConfig;

    public PieFile()
    {

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

    public long getLastModified()
    {
	return file.lastModified();
    }

    public void setLastModified(long lastModified)
    {
	this.lastModified = lastModified;
    }

    public String getRelativeFilePath()
    {
	return relativeFilePath;
    }

    public void setRelativeFilePath(String relativeFilePath)
    {
	this.relativeFilePath = relativeFilePath;
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

	/* if (!pieFile.getMD5().equals(this.getMD5()))
	 {
	 return false;
	 }*/
	return true;
    }

}
