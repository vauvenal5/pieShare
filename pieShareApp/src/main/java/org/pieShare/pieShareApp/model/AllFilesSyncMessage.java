package org.pieShare.pieShareApp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.pieShare.pieShareApp.service.fileService.PieDirectory;
import org.pieShare.pieTools.piePlate.model.message.HeaderMessage;

public class AllFilesSyncMessage extends HeaderMessage
{

	private ArrayList<FileChangedMessage> dirs;
	private UUID id;
	private boolean isRequest;

	public ArrayList<FileChangedMessage> getList()
	{
		if (dirs == null)
		{
			dirs = new ArrayList<>();
		}

		return dirs;
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
