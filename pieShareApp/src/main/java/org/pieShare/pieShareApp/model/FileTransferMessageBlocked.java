package org.pieShare.pieShareApp.model;

public class FileTransferMessageBlocked extends FileTransferMessage
{
    private int blockNumber;

    public int getBlockNumber()
    {
        return blockNumber;
    }

    public void setBlockNumber(int blockNumber)
    {
        this.blockNumber = blockNumber;
    }
    
}