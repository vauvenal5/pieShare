/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model.message;

import java.util.UUID;
import org.pieShare.pieShareApp.model.message.FileChangedMessage;

/**
 *
 * @author richy
 */
public class FileTransferMessage extends FileChangedMessage
{
    private UUID id;
    private byte[] block;
    private int blockSize;

    public int getBlockSize()
    {
        return blockSize;
    }

    public void setBlockSize(int blockSize)
    {
        this.blockSize = blockSize;
    }
    
    public byte[] getBlock()
    {
        return block;
    }

    public void setBlock(byte[] block)
    {
        this.block = block;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }
    
    
    
}
