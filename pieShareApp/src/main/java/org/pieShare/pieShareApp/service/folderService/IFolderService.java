/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.folderService;

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
    
    /**
     * Delete the folder at the given path (string) and all it's content
     * @param path of the folder including its name
     * @throws FolderServiceException 
     */
    void deleteFolder(String path) throws FolderServiceException;
    
    /**
     * Delete the folder at the PieFolders path and all it's content
     * @param pieFolder the relative path + name of the folder to be deleted
     * @throws FolderServiceException 
     */
    void deleteFolder(PieFolder pieFolder) throws FolderServiceException;
    

}
