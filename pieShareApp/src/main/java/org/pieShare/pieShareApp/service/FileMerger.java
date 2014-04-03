/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.api.IFileMerger;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class FileMerger implements IFileMerger
{

    private final PieLogger logger = new PieLogger(FileMerger.class);
    private HashMap<String, PieDirectory> dirs;

    public FileMerger()
    {
	dirs = new HashMap<>();
    }

    @Override
    public void fileCreated(File file)
    {
	if (!file.exists())
	{
	    logger.debug("Chreated file does not exist");
	    return;
	}

	if (file.isDirectory())
	{
	    PieDirectory dir = null;
	    try
	    {
		dir = new PieDirectory(file);
	    }
	    catch (Exception ex)
	    {
		logger.debug("Error in directory check: Message:" + ex.getMessage());
		return;
	    }

	    if (!dirs.containsKey(dir.getRelativeFilePath()))
	    {
		dirs.put(dir.getRelativeFilePath(), dir);
	    }

	}
	else if (file.isFile())
	{
	    PieFile pieFile = new PieFile(file);

	    PieDirectory dir = null;

	    dir = new PieDirectory(pieFile.getFile().getParentFile());

	    if (!dirs.containsKey(dir.getRelativeFilePath()))
	    {
		dirs.put(dir.getRelativeFilePath(), dir);
	    }

	    checkListForNewFile(dirs.get(dir.getRelativeFilePath()).getFiles(), pieFile);
	}
    }

    @Override
    public void fileDeleted(File file)
    {
	PieFile pieFile = new PieFile(file);

	//If file is in Direcotry list it is an Dir, so we delete it and OK
	if (dirs.containsKey(pieFile.getRelativeFilePath()))
	{
	    dirs.remove(pieFile.getRelativeFilePath());
	}
	else
	{
	    //If not in  dir list it is a file, get parent folder.
	    PieDirectory dir = new PieDirectory(file.getParentFile());

	    if(!dirs.containsKey(dir.getRelativeFilePath()))
	    {
		logger.debug("Cannot find folder from file to delete!");
		return;
	    }
	    
	    deleteFileFromList(dirs.get(dir.getRelativeFilePath()).getFiles(), pieFile);
	}
    }

    @Override
    public void fileChanged(File file)
    {
	PieFile pieFile = new PieFile(file);
	
	if(dirs.containsKey(pieFile.getRelativeFilePath()))
	{
	    logger.debug("Changed File is a Folder.");
	    //Changed File is a Folder. 
	    //Ignore
	    return;
	}
	
	PieDirectory dir = new PieDirectory(file.getParentFile());
	
	if(!dirs.containsKey(dir.getRelativeFilePath()))
	{
	    //The folder from changed file is not in the folder list. Error.
	    logger.debug("The folder from changed file is not in the folder list.");
	    return;
	}
	
	checkListForChangedFile(dirs.get(dir.getRelativeFilePath()).getFiles(), pieFile);
    }

    private void checkListForChangedFile(HashMap<String, PieFile> files, PieFile pieFile)
    {
	if(files.containsKey(pieFile.getRelativeFilePath()))
	{
	    files.remove(pieFile.getRelativeFilePath());
	    files.put(pieFile.getRelativeFilePath(), pieFile);
	}
	else
	{
	    fileCreated(pieFile.getFile());
	}
    
    }    
    
    private void checkListForNewFile(HashMap<String, PieFile> files, PieFile pieFile)
    {
	if (files.containsKey(pieFile.getRelativeFilePath()))
	{
	    if (files.get(pieFile.getRelativeFilePath()).equals(pieFile))
	    {
		//Is Exectly same file, do nothing
	    }
	    else
	    {
		files.remove(pieFile.getRelativeFilePath());
		files.put(pieFile.getRelativeFilePath(), pieFile);
	    }
	}
	else
	{
	    files.put(pieFile.getRelativeFilePath(), pieFile);
	}
    }

    private void deleteFileFromList(HashMap<String, PieFile> files, PieFile pieFile)
    {
	if (files.containsKey(pieFile.getRelativeFilePath()))
	{
	    if (files.get(pieFile.getRelativeFilePath()).equals(pieFile))
	    {
		files.remove(pieFile.getRelativeFilePath());
	    }
	    else
	    {
		//File not the same. 
	    }
	}
	else
	{
	    //File is not in list
	}
    }
}
