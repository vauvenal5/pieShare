/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.folderService;

import java.io.File;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 * @author daniela
 */
public class FolderService extends FilderServiceBase implements IFolderService {

    //TODO: refactor to utility functions
   /* protected IPieShareConfiguration configuration;
    protected IUserService userService;

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

	//todo: redundant code!!! use fileService
    public String getAbsolutePath(PieFolder pieFolder) {
        if(configuration == null) {
            PieUser user = userService.getUser();
            this.configuration = user.getPieShareConfiguration();
        }
        File localFolder = new File(configuration.getWorkingDir(), pieFolder.getRelativePath());
        return localFolder.getPath();
    }*/

    @Override
    public void createFolder(PieFolder pieFolder) throws FolderServiceException {
        File newFolder = getAbsolutePath(pieFolder);
        createLocalFolder(newFolder);
    }

    @Override
    public void createFolder(String path) throws FolderServiceException {
        File newFolder = new File(path);
        createLocalFolder(newFolder);
    }

    //calles by the createFolder methods
    private void createLocalFolder(File newFolder) {

        if (!newFolder.exists()) {
            newFolder.mkdirs();
            //TODO: what if the folder can't be created?
            PieLogger.trace(this.getClass(), "Folder created!");
        } else {
            PieLogger.debug(this.getClass(), "Folder exits already?!");
        }
    }

   /* @Override
    public void deleteFolder(String path) throws FolderServiceException {
        deleteRecursive();
        File folderToDelete = new File(path);
        deleteLocalFolder(folderToDelete);
        
       
    }*/

    @Override
    public void deleteFolder(PieFolder pieFolder) throws FolderServiceException {
        deleteRecursive(pieFolder);
        //File foldertoDelete = getAbsolutePath(pieFolder);
        //deleteLocalFolder(foldertoDelete);
    }

    /*
    private void deleteLocalFolder(File folderToDelete) {
        boolean deleted = false;
        
        if (folderToDelete.exists()) {
            deleted = folderToDelete.delete();
            PieLogger.trace(this.getClass(), "Folder deleted! " + folderToDelete.getPath());
        }
      
        if (!deleted) {
            PieLogger.debug(this.getClass(), "Folder couldn't be deleted: " + folderToDelete.getPath());
        }
    }
    */

}
