/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.api.IFileWatcherService;
import org.pieShare.pieShareApp.configuration.Configuration;
import org.pieShare.pieTools.pieUtilities.service.hashService.MD5Service;

/**
 *
 * @author richy
 */
public class PieFile
{

	private File file;
	private String md5 = "";

	public PieFile(File file)
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
	}

	public PieFile(String filePath)
	{

		this.file = new File(filePath);

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
		File workingDir = Configuration.getWorkingDirectory();

		Path pathBase = new File(workingDir.getAbsolutePath()).toPath(); // Paths.get("/var/data");
		
		Path pathAbsolute = file.toPath(); // Paths.get("/var/data/stuff/xyz.dat");
		
		Path pathRelative = pathBase.relativize(pathAbsolute);
		return pathRelative.toString();
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
