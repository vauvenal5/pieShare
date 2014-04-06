package org.pieShare.pieShareApp.model;

import java.util.HashMap;
import java.util.UUID;
import org.pieShare.pieShareApp.service.PieDirectory;
import org.pieShare.pieTools.piePlate.model.message.HeaderMessage;

public class AllFilesMessage extends HeaderMessage
{
	private HashMap<String, PieDirectory> dirs;
	private UUID id;
	private boolean isRequest;

	public HashMap<String, PieDirectory> getDirs()
	{
		return dirs;
	}

	public void setDirs(HashMap<String, PieDirectory> dirs)
	{
		this.dirs = dirs;
	}

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public boolean isIsRequest()
	{
		return isRequest;
	}

	public void setIsRequest(boolean isRequest)
	{
		this.isRequest = isRequest;
	}
	
	
}
