/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.pieShare.pieShareApp.model.entities.PieFileEntity;
import org.pieShare.pieShareApp.model.entities.PieFolderEntity;
import org.pieShare.pieShareApp.model.pieFilder.PieFilder;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.database.DAOs.PieFileDAO;
import org.pieShare.pieShareApp.service.database.DAOs.PieFolderDAO;
import org.pieShare.pieShareApp.service.database.api.IModelEntityConverterService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.folderService.IFolderService;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author RicLeo00
 */
public class PieFilderDBService {

    private IModelEntityConverterService converterService;
    private PieFileDAO pieFileDAO;
    private PieFolderDAO pieFolderDAO;
    private IUserService userService;
    private IFileService fileService;
    private IFolderService folderService;

    public void setConverterService(IModelEntityConverterService converterService) {
        this.converterService = converterService;
    }

    public void setPieFileDAO(PieFileDAO pieFileDAO) {
        this.pieFileDAO = pieFileDAO;
    }

    public void setPieFolderDAO(PieFolderDAO pieFolderDAO) {
        this.pieFolderDAO = pieFolderDAO;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    public void setFileService(IFileService fileService) {
        this.fileService = fileService;
    }

    public void setFolderService(IFolderService folderService) {
        this.folderService = folderService;
    }

    public void persistFilder(PieFilder filder) throws SQLException, IOException {
        File file = new File(userService.getUser().getPieShareConfiguration().getWorkingDir(), filder.getRelativePath());
        String parent = recusriveFilePersister(file.getParentFile(), filder);

        if (filder instanceof PieFile) {
            mergePieFile((PieFile) filder, parent);
        } else {
            checkOrSaveFolder((PieFolder) filder, parent);
        }
    }

    private String recusriveFilePersister(File file, PieFilder filder) {
        String parentID = null;

        try {
            if (file.getParentFile() == null) {
                return null;
            }
            if (!file.getParentFile().getCanonicalPath().equals(userService.getUser().getPieShareConfiguration().getWorkingDir().getCanonicalPath())) {
                parentID = recusriveFilePersister(file.getParentFile(), filder);
            }
            /*if (file.getParentFile() != null) {//!file.getCanonicalPath().equals(userService.getUser().getPieShareConfiguration().getWorkingDir().getCanonicalPath())) {
                parentID = recusriveFilePersister(file.getParentFile());
            }*/

            //Parent ID == null means it is the root folder!
            /* if (parentID == null) {
                return checkOrSaveFolder(file, true, null);
            }*/
            //Check if Dir or file and persist it to DB
            if (file.isDirectory()) {
                return checkOrSaveFolder(folderService.generatePieFolder(file), parentID);
            }
            /*else {
                return mergePieFile(file, parentID);//return parentID; //checkOrSaveFile(file, parentID);
            }*/

        } catch (SQLException | IOException ex) {
            PieLogger.error(this.getClass(), "Error pesisting Filder", ex);
        }
        return parentID;
    }

    private String checkOrSaveFolder(PieFolder file, String parent) throws SQLException {
        boolean isRoot = false;
        if (parent == null) {
            parent = "root";
            isRoot = true;
        }
        PieFolderEntity rootEntity = pieFolderDAO.findFolderWhereNameANDIsRoot(file.getName(), isRoot, parent);

        if (rootEntity == null) {
            rootEntity = converterService.convertToEntity(file);
            rootEntity.setParent(parent);
            rootEntity.setIsRoot(isRoot);
            pieFolderDAO.savePiePieFolder(rootEntity);
        }
        return rootEntity.getId();
    }

    private String mergePieFile(PieFile file, String parent) throws SQLException, IOException {

        if (parent == null) {
            parent = "root";
        }

        PieFileEntity fileEntity = pieFileDAO.findAllWhereNameAndParent(file.getName(), parent);

        if (fileEntity == null) {
            fileEntity = converterService.convertToEntity(file);
            fileEntity.setParent(parent);
            pieFileDAO.savePieFile(fileEntity);
        }

        pieFileDAO.updatePieFile(fileEntity);

        return fileEntity.getId();
    }

    public List<PieFile> findAllPieFiles() throws SQLException {
        ArrayList<PieFile> files = new ArrayList<>();

        for (PieFileEntity entity : pieFileDAO.findAllPieFiles()) {
            files.add(findFile(entity.getId()));
        }

        return files;
    }

    public PieFile findFile(String id) throws SQLException {
        PieFileEntity file = pieFileDAO.findPieFileById(id);

        if (file == null) {
            return null;
        }

        if (file.getParent().equals("root")) {
            PieFile pieFile = converterService.convertFromEntity(file);
            pieFile.setRelativePath(String.format("%s%s", File.separator, file.getFileName()));
            return pieFile;
        }

        PieFolder folder = findFolder(file.getParent());

        PieFile pieFile = converterService.convertFromEntity(file);
        pieFile.setRelativePath(String.format("%s%s", folder.getRelativePath(), file.getFileName()));
        return pieFile;
    }

    public List<PieFolder> findAllPieFolders() throws SQLException {
        ArrayList<PieFolder> folders = new ArrayList<>();

        for (PieFolderEntity entity : pieFolderDAO.findAllPieFolders()) {
            folders.add(findFolder(entity.getId()));
        }

        return folders;
    }

    public PieFolder findFolder(String id) throws SQLException {

        String relativePath;

        PieFolderEntity folder = pieFolderDAO.findPieFolderById(id);

        if (folder == null) {
            return null;
        }

        PieFolder pieFolder = converterService.convertFromEntity(folder);

        if (folder.isIsRoot()) {
            pieFolder.setRelativePath(String.format("%s%s%s", File.separator, folder.getFolderName(), File.separator));
            return pieFolder;
        }

        PieFolder parent = findFolder(folder.getParent());
        relativePath = String.format("%s%s%s", parent.getRelativePath(), pieFolder.getName(), File.separator);
        pieFolder.setRelativePath(relativePath);
        return pieFolder;
    }

    public void removePieFolder(PieFolder folder) throws SQLException {
        PieFolderEntity entity = this.converterService.convertToEntity(folder);
        pieFolderDAO.deletePieFolder(entity.getId());
    }
}
