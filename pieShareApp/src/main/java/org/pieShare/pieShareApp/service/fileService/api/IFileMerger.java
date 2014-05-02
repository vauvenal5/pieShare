/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import org.pieShare.pieShareApp.model.message.FileChangedMessage;
import org.pieShare.pieShareApp.service.fileService.PieDirectory;
import org.pieShare.pieShareApp.service.fileService.PieFile;

/**
 *
 * @author Richard
 */
public interface IFileMerger
{

    public void fileCreated(File file);

    public void fileDeleted(File file);

    public void fileChanged(File file);

    public void setFileService(IFileService fileService);

    public HashMap<String, PieDirectory> getDirs();

    public void remoteFileChanged(FileChangedMessage fileChangedMessage);
    
    public PieFile getFile(String relativeFilePath) throws FileNotFoundException;
}
