/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService.api;

import java.io.File;
import java.util.HashMap;
import org.pieShare.pieShareApp.model.FileChangedMessage;
import org.pieShare.pieShareApp.service.fileService.PieDirectory;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanServiceException;

/**
 *
 * @author Richard
 */
public interface IFileMerger
{

    public void fileCreated(File file) throws BeanServiceException;

    public void fileDeleted(File file) throws BeanServiceException;

    public void fileChanged(File file) throws BeanServiceException;

    public void setFileService(IFileService fileService);

    public HashMap<String, PieDirectory> getDirs();

    public void remoteFileChanged(FileChangedMessage fileChangedMessage) throws BeanServiceException;
}
