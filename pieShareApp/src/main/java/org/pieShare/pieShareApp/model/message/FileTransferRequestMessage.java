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
public class FileTransferRequestMessage extends FileChangedMessage
{

    private UUID id;

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }
}
