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
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.pieShare.pieShareApp.service.userService.UserService;
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

    public void persistFilder(PieFilder filder) throws SQLException {
        String parent = recusriveFilePersister(new File(userService.getUser().getPieShareConfiguration().getWorkingDir(), filder.getRelativePath()));

        if (filder instanceof PieFile) {
            PieFileEntity pieFileEntity = converterService.convertToEntity((PieFile) filder);
            pieFileEntity.setParent(parent);
            pieFileDAO.updatePieFile(pieFileEntity);
        }
    }

    private String recusriveFilePersister(File file) {

        String parentID = null;

        try {
            if (!file.getCanonicalPath().equals(userService.getUser().getPieShareConfiguration().getWorkingDir().getCanonicalPath())) {
                parentID = recusriveFilePersister(file.getParentFile());
            }

            //Parent ID == null means it is the root folder!
            if (parentID == null) {
                return checkOrSaveFolder(file, true, null);
            }

            //Check if Dir or file and persist it to DB
            if (file.isDirectory()) {
                return checkOrSaveFolder(file, false, parentID);
            } else {
                return parentID; //checkOrSaveFile(file, parentID);
            }

        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error pesisting Filder", ex);
        } catch (IOException ex) {
            PieLogger.error(this.getClass(), "Error pesisting Filder", ex);
        }
        return null;
    }

    private String checkOrSaveFolder(File file, boolean isRoot, String parent) throws SQLException {
        PieFolderEntity rootEntity = pieFolderDAO.findFolderWhereNameANDIsRoot(file.getName(), isRoot, parent);

        if (rootEntity == null) {
            rootEntity = converterService.convertToEntity(folderService.generatePieFolder(file));
            pieFolderDAO.savePiePieFolder(rootEntity);
        }
        return rootEntity.getId();
    }

    private String checkOrSaveFile(File file, String parent) throws SQLException, IOException {
        PieFileEntity fileEntity = pieFileDAO.findAllWhereNameAndParent(file.getName(), parent);

        if (fileEntity == null) {
            fileEntity = converterService.convertToEntity(fileService.getPieFile(file));
            pieFileDAO.savePieFile(fileEntity);
        }
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
            pieFolder.setRelativePath(File.pathSeparator);
            return pieFolder;
        }

        PieFolder parent = findFolder(folder.getParent());
        relativePath = String.format("%s%s%s", parent.getRelativePath(), parent.getName(), File.pathSeparator);
        pieFolder.setRelativePath(relativePath);
        return pieFolder;
    }

    public void removePieFolder(PieFolder folder) throws SQLException {
        PieFolderEntity entity = this.converterService.convertToEntity(folder);
            pieFolderDAO.deletePieFolder(entity.getId());
    }
}
