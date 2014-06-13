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

	private byte[] md5;
	private String relativeFilePath;
	private String fileName;
	private long lastModified;
	
	public PieFile()
	{

	}

	public byte[] getMd5()
	{
		return md5;
	}

	public void setMd5(byte[] md5)
	{
		this.md5 = md5;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public long getLastModified()
	{
		return lastModified;
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

}
