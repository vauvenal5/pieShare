/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model;

import java.util.UUID;

/**
 *
 * @author richy
 */
public class FileTransferMessage extends FileChangedMessage
{
    private UUID id;
    private byte[] block;

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
