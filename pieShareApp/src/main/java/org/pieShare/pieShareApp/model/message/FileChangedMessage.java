package org.pieShare.pieShareApp.model.message;

import org.pieShare.pieTools.piePlate.model.message.HeaderMessage;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;

public class FileChangedMessage extends HeaderMessage
{

    private String relativeFilePath;
    private long lastModified;
    private String md5;
    private FileChangedTypes changedType;

    public void setChangedType(FileChangedTypes changedType)
    {
	this.changedType = changedType;
    }
    
    public FileChangedTypes getChangedType()
    {
	return this.changedType;
    }

    public String getRelativeFilePath()
    {
	return relativeFilePath;
    }

    public void setRelativeFilePath(String relativeFilePath)
    {
	this.relativeFilePath = relativeFilePath;
    }

    public long getLastModified()
    {
	return lastModified;
    }

    public void setLastModified(long lastModified)
    {
	this.lastModified = lastModified;
    }

    public String getMd5()
    {
	return md5;
    }

    public void setMd5(String md5)
    {
	this.md5 = md5;
    }

}
