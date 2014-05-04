/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieShareApp.service.fileService.api.IFileWatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Richard
 */
public class PieDirectory
{

    private HashMap<String, PieFile> files;
    private IFileWatcherService watcherService;
    private File dir;
    private String relativeFilePath;
    private IPieShareAppConfiguration pieAppConfig;

    public PieDirectory()
    {
    }

    public void init(File dir)
    {
        this.dir = dir;
        this.files = new HashMap<>();

        File workingDir = pieAppConfig.getWorkingDirectory();
        Path pathBase = new File(workingDir.getAbsolutePath()).toPath();
        Path pathAbsolute = dir.toPath(); // Paths.get("/var/data/stuff/xyz.dat");
        Path pathRelative = pathBase.relativize(pathAbsolute);
        this.relativeFilePath = pathRelative.toString();
    }

    @Autowired
    @Qualifier("pieShareAppConfiguration")
    public void setPieShareAppConfiguration(IPieShareAppConfiguration pieShareAppConfiguration)
    {
        this.pieAppConfig = pieShareAppConfiguration;
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
        return relativeFilePath;
    }
	
	public File getFile()
	{
		return dir;
	}
}
