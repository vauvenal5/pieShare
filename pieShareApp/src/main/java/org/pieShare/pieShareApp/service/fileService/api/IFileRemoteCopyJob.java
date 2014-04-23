/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.DataFormatException;
import org.pieShare.pieShareApp.model.message.FileTransferMessageBlocked;

/**
 *
 * @author richy
 */
public interface IFileRemoteCopyJob
{

    public void newDataArrived(FileTransferMessageBlocked msg) throws IOException, DataFormatException;

    public void cleanUP();
             
}
