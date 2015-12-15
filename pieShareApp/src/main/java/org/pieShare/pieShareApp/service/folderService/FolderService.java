/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.folderService;

import java.io.File;
import java.nio.file.Path;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareConfiguration;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 * @author daniela
 */
public class FolderService implements IFolderService{
    
    //TODO: refactor to utility functions
    protected IPieShareConfiguration configuration;
    protected IUserService userService;

    
    public void setUserService(IUserService userService) {
	this.userService = userService;
    }
    
    public Path getAbsolutePath(PieFolder pieFolder) {
        if(configuration == null) {
            PieUser user = userService.getUser();
            this.configuration = user.getPieShareConfiguration();
        }
        File localFolder = new File(configuration.getWorkingDir(), pieFolder.getRelativePath());
        return localFolder.toPath();
    }


    @Override
    public void createFolder(PieFolder pieFolder) throws FolderServiceException {
        File newFolder = new File(getAbsolutePath(pieFolder).toString());
        createLocalFolder(newFolder);
    }

    @Override
    public void createFolder(String path) throws FolderServiceException {
        File newFolder = new File(path);
        createLocalFolder(newFolder);
    }
    
    
    //calles by the createFolder methods
    private void createLocalFolder(File newFolder) {
        
        if(!newFolder.exists()) {
            newFolder.mkdirs();
            //TODO: what if the folder can't be created?
            PieLogger.trace(this.getClass(), "Folder created!");
        } else {
            PieLogger.debug(this.getClass(), "Folder exits already?!");
        }
    }

}
