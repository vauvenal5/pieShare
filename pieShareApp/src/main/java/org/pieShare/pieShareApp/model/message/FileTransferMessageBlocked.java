package org.pieShare.pieShareApp.model.message;

public class FileTransferMessageBlocked extends FileTransferMessage
{
    private int blockNumber;
    private boolean isLastEmptyMessage = false;

    public int getBlockNumber()
    {
        return blockNumber;
    }

    public void setBlockNumber(int blockNumber)
    {
        this.blockNumber = blockNumber;
    }

    public boolean isIsLastEmptyMessage()
    {
        return isLastEmptyMessage;
    }

    public void setIsLastEmptyMessage(boolean isLastEmptyMessage)
    {
        this.isLastEmptyMessage = isLastEmptyMessage;
    }
    
    
    
}