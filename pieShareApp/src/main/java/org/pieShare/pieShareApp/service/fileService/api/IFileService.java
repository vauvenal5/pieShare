/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService.api;

import java.io.File;
import java.io.FileNotFoundException;
import org.pieShare.pieShareApp.service.fileService.PieFile;

/**
 *
 * @author richy
 */
public interface IFileService
{

	//public void remoteFileChange(FileChangedMessage message);
    public PieFile genPieFile(File file) throws FileNotFoundException;

    public void localFileChange(File file);

//	public void remoteAllFilesSyncRequest(AllFilesSyncMessage msg);
//	public void sendAllFilesSyncRequest();
}
