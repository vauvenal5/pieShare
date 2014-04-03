/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Iterator;
import org.pieShare.pieShareApp.api.IFileWatcherService;
import org.pieShare.pieShareApp.configuration.Configuration;

/**
 *
 * @author Richard
 */
public class PieDirectory
{

	private HashMap<String, PieFile> files;
	private IFileWatcherService watcherService;
	private File dir;

	public PieDirectory(File dir) throws Exception
	{
		if (!dir.exists() || !dir.isDirectory())
		{
			throw new Exception("Is no Directory, or does not exist.");
		}

		this.dir = dir;
		this.files = new HashMap<>();
	}

	public void setFileWatcherService(IFileWatcherService watchService)
	{
		this.watcherService = watchService;
	}

	public HashMap<String, PieFile> getFiles()
	{
		return files;
	}

	public String getRelativeFilePath()
	{
		File workingDir = Configuration.getWorkingDirectory();
		//Path pathBase = workingDir.toPath(); // Paths.get("/var/data");
		
		Path pathBase = new File(workingDir.getAbsolutePath()).toPath();
		
		
		Path pathAbsolute = dir.toPath(); // Paths.get("/var/data/stuff/xyz.dat");
		
		Path pathRelative = pathBase.relativize(pathAbsolute);
		return pathRelative.toString();
	}
}
