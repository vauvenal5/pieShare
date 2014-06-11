/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.tempFolderService;

import java.io.File;
import org.pieShare.pieTools.pieUtilities.service.tempFolderService.api.ITempFolderService;

/**
 *
 * @author Richard
 */
public class TempFolderService implements ITempFolderService
{

    @Override
    public File createTempFolder(String fileName, File parentDir )throws Exception
    {
	if (!parentDir.exists())
	{
	    throw new Exception("Given temp folder does not exist");
	}

	String blokDirName = ".copyJobf_" + fileName;
	File blockDir = new File(parentDir, fileName);

	boolean found = false;
	int index = 0;
	while (!found)
	{
	    if (blockDir.exists())
	    {
		blockDir = new File(parentDir, blokDirName + "_" + index++);
	    }
	    else
	    {
		blockDir.mkdirs();
		found = true;
	    }
	}

	return blockDir;
    }
}
