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

    @Override
    public PieFolder generatePieFolder(File file) {
        PieFolder pieFolder = new PieFolder();
        pieFolder.setName(file.getName());
        pieFolder.setRelativePath(this.relativizeFilePath(file));
        return pieFolder;
    }

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

    @Override
    public void deleteFolder(PieFolder pieFolder) throws FolderServiceException {
        deleteRecursive(pieFolder);
    }

    @Override
    public void deleteFolder(File file) throws FolderServiceException {
        deleteRecursive(file);
    }

}
