/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service;

import java.io.File;
import java.util.HashMap;
import org.pieShare.pieShareApp.api.IFileMerger;
import org.pieShare.pieShareApp.api.IFileService;
import org.pieShare.pieShareApp.configuration.Configuration;
import org.pieShare.pieShareApp.model.FileChangedMessage;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;

/**
 *
 * @author Richard
 */
public class FileMerger implements IFileMerger
{

	private final PieLogger logger = new PieLogger(FileMerger.class);
	private IFileService fileService;
	private HashMap<String, PieDirectory> dirs;

	public FileMerger()
	{
		dirs = new HashMap<>();
	}

	@Override
	public void setFileService(IFileService fileService)
	{
		this.fileService = fileService;
	}

	@Override
	public void fileCreated(File file)
	{
		if (!file.exists())
		{
			//ToDo: It must not exist, because it can be from an remote location. But check this when we insert remote merger.
			logger.debug("Create File: File does not exist, maybe from remote location.");
			//return;
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
		else
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

			if (!dirs.containsKey(dir.getRelativeFilePath()))
			{
				logger.debug("File Delete: Cannot find folder from file to delete, is alredy delted!");
				return;
			}

			deleteFileFromList(dirs.get(dir.getRelativeFilePath()).getFiles(), pieFile);
		}
	}

	@Override
	public void fileChanged(File file)
	{
		PieFile pieFile = new PieFile(file);

		if (dirs.containsKey(pieFile.getRelativeFilePath()))
		{
			logger.debug("ChangedFile: Changed File is a Folder. Do nothing!!. ");
			//Changed File is a Folder. 
			//Ignore
			return;
		}

		PieDirectory dir = new PieDirectory(file.getParentFile());

		if (!dirs.containsKey(dir.getRelativeFilePath()))
		{
			//The folder from changed file is not in the folder list. Error.
			logger.debug("File Changed: The folder from changed file is not in the folder list, add foder to list!!.");

			dirs.put(dir.getRelativeFilePath(), dir);

			return;
		}

		checkListForChangedFile(dirs.get(dir.getRelativeFilePath()).getFiles(), pieFile);
	}

	private void checkListForChangedFile(HashMap<String, PieFile> files, PieFile pieFile)
	{
		if (files.containsKey(pieFile.getRelativeFilePath()))
		{
			if (files.get(pieFile.getRelativeFilePath()).equals(pieFile))
			{
				logger.debug("Changed File: File is exactly same. Do not Change.");
				//Is Exectly same file, do nothing
				return;
			}

			logger.debug("Changed File: Remove old and add new file");

			files.remove(pieFile.getRelativeFilePath());
			files.put(pieFile.getRelativeFilePath(), pieFile);

			sendNewMessage(FileChangedTypes.FILE_MODIFIED, pieFile);
		}
		else
		{
			logger.debug("Changed File: File does not exist call fileCreated()");
			fileCreated(pieFile.getFile());
		}
	}

	private void checkListForNewFile(HashMap<String, PieFile> files, PieFile pieFile)
	{
		if (files.containsKey(pieFile.getRelativeFilePath()))
		{
			logger.debug("Created File: File is alrey in list, call fileChanged().");
			checkListForChangedFile(files, pieFile);
		}
		else
		{
			logger.debug("Created File: Add new created file");
			files.put(pieFile.getRelativeFilePath(), pieFile);
			sendNewMessage(FileChangedTypes.FILE_CREATED, pieFile);
		}
	}

	private void deleteFileFromList(HashMap<String, PieFile> files, PieFile pieFile)
	{
		if (files.containsKey(pieFile.getRelativeFilePath()))
		{
			if (files.get(pieFile.getRelativeFilePath()).equals(pieFile))
			{
				logger.debug("Deleted File: Delete file from list");
				files.remove(pieFile.getRelativeFilePath());
				sendNewMessage(FileChangedTypes.FILE_DELETED, pieFile);
			}
			else
			{
				logger.debug("Deleted File: Files are not equal, do not remove file");
			}
		}
		else
		{
			logger.debug("Deleted File: File to delete does not exist.");
		}
	}

	private void sendNewMessage(FileChangedTypes type, PieFile file)
	{
		FileChangedMessage msg = new FileChangedMessage();
		msg.setChangedType(type);
		msg.setLastModified(file.getLastModified());
		msg.setMd5(file.getMD5());
		msg.setRelativeFilePath(file.getRelativeFilePath());

		logger.debug("Send Messahe: Send " + type.FILE_CREATED.toString() + " event. FileName: " + file.getFile().getName());

		fileService.localFileChange(msg);
	}

	@Override
	public void remoteFileChanged(FileChangedMessage fileChangedMessage)
	{
		logger.debug("Remote File Changed: Remote file has changed. Check if needed.");
		if (dirs.containsKey(fileChangedMessage.getRelativeFilePath()))
		{
			logger.debug("Remote File Changed: File is Dir. Do nothing.");
			return;
		}

		File file = new File(Configuration.getWorkingDirectory(), fileChangedMessage.getRelativeFilePath());
		PieFile createdFile = new PieFile(file);

		PieDirectory dir = new PieDirectory(file.getParentFile());

		if (dirs.containsKey(dir.getRelativeFilePath()))
		{
			if (dirs.get(dir.getRelativeFilePath()).getFiles().containsKey(createdFile.getRelativeFilePath()))
			{
				PieFile localFile = dirs.get(dir.getRelativeFilePath()).getFiles().get(createdFile.getRelativeFilePath());
				
				if(localFile.getLastModified() < fileChangedMessage.getLastModified())
				{
					fileChanged(file);
				}
				else
				{
					//Local File is newer than remote file, so do nothing:
					//ToDo: Check this when implementing an better file merger for remote file.
					logger.debug("Remote File Changed: Remote file is older then local. Do nothing.");
				}
				
			}
			else
			{
				fileCreated(file);
			}
		}
		else
		{
			fileCreated(file);
		}

	}

	@Override
	public HashMap<String, PieDirectory> getDirs()
	{
		return dirs;
	}
}
