/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.folderService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;

/**
 * Provides all services regarding a PieFolder
 * @author daniela
 */
public interface IFolderService {
    /**
     * Create a local folder at the PieFolder local path
     * @param pieFolder the relative path is where the folder will be created.
     * @throws FolderServiceException 
     */
    void createFolder(PieFolder pieFolder) throws FolderServiceException;
    
    /**
     * Create a folder at the given path (string)
     * @param path of the folder including its name
     * @throws FolderServiceException 
     */
    void createFolder(String path) throws FolderServiceException;

}
