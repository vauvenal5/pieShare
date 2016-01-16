/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.entities.PieFileEntity;
import org.pieShare.pieShareApp.model.entities.PieFolderEntity;
import org.pieShare.pieShareApp.model.pieFilder.PieFilder;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.database.DAOs.PieFileDAO;
import org.pieShare.pieShareApp.service.database.DAOs.PieFolderDAO;
import org.pieShare.pieShareApp.service.database.api.IModelEntityConverterService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.folderService.IFolderService;
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
    private UserService userService;
    private IFileService fileService;
    private IFolderService folderService;

    private void persistFilder(PieFilder filder) throws SQLException {
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
}
