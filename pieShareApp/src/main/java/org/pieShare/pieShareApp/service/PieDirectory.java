/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service;

import java.util.HashMap;
import org.pieShare.pieShareApp.api.IFileWatcherService;

/**
 *
 * @author Richard
 */
public class PieDirectory
{
	private HashMap<String, PieFile> files;
	private IFileWatcherService watcherService;
	
	public PieDirectory()
	{
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
	
}
