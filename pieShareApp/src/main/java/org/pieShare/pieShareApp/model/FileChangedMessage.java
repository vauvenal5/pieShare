package org.pieShare.pieShareApp.model;

import org.pieShare.pieShareApp.service.PieFile;
import org.pieShare.pieShareApp.service.PieFileInfo;
import org.pieShare.pieTools.piePlate.model.message.HeaderMessage;
import org.pieShare.pieTools.pieUtilities.utils.FileChangedTypes;

public class FileChangedMessage extends HeaderMessage
{

    private PieFile fileInfo;
    private FileChangedTypes fileChangedType;

    public void setFileChangedType(FileChangedTypes type)
    {
        this.fileChangedType = type;
    }

    public FileChangedTypes getFileChangedType()
    {
        return this.fileChangedType;
    }

    public void setPieFile(PieFile fileInfo)
    {
        this.fileInfo = fileInfo;
    }

    public PieFile getPieFile()
    {
        return fileInfo;
    }
}
