/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService.api;

import org.pieShare.pieShareApp.model.message.AllFilesSyncMessage;
import org.pieShare.pieShareApp.model.message.FileChangedMessage;
import org.pieShare.pieShareApp.model.message.FileTransferMessageBlocked;
import org.pieShare.pieShareApp.model.message.FileTransferRequestMessage;
import org.pieShare.pieShareApp.service.fileService.PieFile;

/**
 *
 * @author richy
 */
public interface IFileService
{

    public void remoteFileChange(FileChangedMessage message);

    public void localFileChange(FileChangedMessage message);

    public void remoteAllFilesSyncRequest(AllFilesSyncMessage msg);

    public void sendAllFilesSyncRequest();

    public void fileTransferRequestReceived(FileTransferRequestMessage msg);

    public void fileTransfereMessage(FileTransferMessageBlocked msg);
    
    public void sendFileTransferRequenst(PieFile piefile);
}
