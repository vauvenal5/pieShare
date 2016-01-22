/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.entities.FilterEntity;
import org.pieShare.pieShareApp.model.entities.PieUserEntity;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.database.DAOs.FileFilterDAO;
import org.pieShare.pieShareApp.service.database.DAOs.PieUserDAO;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.service.database.api.IModelEntityConverterService;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author RicLeo00
 */
public class DatabaseService implements IDatabaseService {

    private IModelEntityConverterService modelEntityConverterService;
    private PieFilderDBService pieFilderDBService;

    private PieUserDAO pieUserDAO;
    private FileFilterDAO fileFilterDAO;

    public void setPieUserDAO(PieUserDAO pieUserDAO) {
        this.pieUserDAO = pieUserDAO;
    }

    public void setFileFilterDAO(FileFilterDAO fileFilterDAO) {
        this.fileFilterDAO = fileFilterDAO;
    }

    public DatabaseService() {
    }

    public void setPieFilderDBService(PieFilderDBService pieFilderDBService) {
        this.pieFilderDBService = pieFilderDBService;
    }

    @Override
    public void setConverterService(IModelEntityConverterService converter) {
        this.modelEntityConverterService = converter;
    }

    @Override
    public void persistPieUser(PieUser model) {
        try {
            pieUserDAO.savePieUser(modelEntityConverterService.convertToEntity(model));
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Persisting PieUser", ex);
        }
    }

    @Override
    public List<PieUser> findAllPieUser() {

        try {
            ArrayList<PieUser> models = new ArrayList<>();
            for (PieUserEntity en : pieUserDAO.findAllPieUsers()) {
                try {
                    models.add(modelEntityConverterService.convertFromEntity(en));
                } catch (Exception ex) {
                    PieLogger.error(this.getClass(), "Error find all PieUsers", ex);
                }
            }

            return models;
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Persistung PieUser", ex);
        }
        return null;
    }

    @Override
    public void removePieUser(PieUser user) {
        PieUserEntity ent;
        ent = modelEntityConverterService.convertToEntity(user);
        try {
            pieUserDAO.deletePieUser(ent);
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Deleting PieUser", ex);
        }
    }

    @Override
    public void mergePieUser(PieUser user) {
        PieUserEntity entity;
        entity = modelEntityConverterService.convertToEntity(user);
        try {
            if (pieUserDAO.findPieUserById(user.getCloudName()) != null) {
                pieUserDAO.updatePieUser(entity);
            } else {
                pieUserDAO.savePieUser(entity);
            }
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Merging PieUser", ex);
        }
    }

    @Override
    public void persistFileFilter(IFilter filter) {
        try {
            fileFilterDAO.saveFilter((FilterEntity) filter);
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Persisting File Filter", ex);
        }
    }

    @Override
    public void removeFileFilter(IFilter filter) {
        try {
            fileFilterDAO.deleteFilter((FilterEntity) filter);
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Deleting File Filter", ex);
        }
    }

    @Override
    public List<IFilter> findAllFilters() {

        List<IFilter> filters = new ArrayList<>();

        try {
            for (FilterEntity ent : fileFilterDAO.findAllFilter()) {
                filters.add(modelEntityConverterService.convertFromEntity(ent));
            }

        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Find All File Filters", ex);
        }
        return filters;
    }

    @Override
    public PieFile findPieFileByRelativeFilePath(PieFile file) {
        try {
            return this.pieFilderDBService.findFileByRelativePath(file.getRelativePath());
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Finding one PieFile", ex);
        }
        return null;
    }

    @Override
    public List<PieFile> findPieFileByHash(byte[] hash) {
        List<PieFile> files = new ArrayList<>();

        try {
            for (PieFileEntity entity : pieFileDAO.findByMd5(hash)) {
                files.add(this.modelEntityConverterService.convertFromEntity(entity));
            }
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error finding all PieFiles by MD5!", ex);

        }
        return files;
    }

    @Override
    public void mergePieFile(PieFile file) {
        try {
            pieFilderDBService.mergePieFile(file);
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Updating PieFile", ex);
        }
    }

    @Override
    public void persistPieFile(PieFile file) {
        try {
            pieFilderDBService.persistFilder(file);
        } catch (SQLException | IOException ex) {
            PieLogger.error(this.getClass(), "Error Persisting PieFile", ex);
        }
    }

    @Override
    public List<PieFile> findAllUnsyncedPieFiles() {
        try {
            return pieFilderDBService.findAllUnsyncedPieFiles();
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error findAllUnsyncedPieFiles", ex);
        }
        return new ArrayList<>();
    }

    @Override
    public void resetAllPieFileSynchedFlags() {
        try {
            pieFilderDBService.resetAllPieFileSynchedFlags();
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error reseting all Synched Flags", ex);
        }
    }

    @Override
    public List<PieFile> findAllPieFiles() {
        try {
            return pieFilderDBService.findAllPieFiles();
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error finding all PieFiles!", ex);
        }
        return new ArrayList<>();
    }

    @Override
    public void persistPieFolder(PieFolder folder) {
        try {
            pieFilderDBService.persistFilder(folder);
        } catch (SQLException | IOException ex) {
            PieLogger.error(this.getClass(), "Error Persisting PieFolder!", ex);
        }
    }

    @Override
    public void mergePieFolder(PieFolder folder) {
        try {
            pieFilderDBService.mergePieFolder(folder);
        } catch (SQLException | IOException ex) {
            PieLogger.error(this.getClass(), "Error Merging PieFolder!", ex);
        }
    }

    @Override
    public PieFolder findPieFolderByRelativeFilePath(PieFolder folder) {
        try {
            return pieFilderDBService.findFolderByRelativePath(folder.getRelativePath());
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Finding PieFolder!", ex);
        }
        return null;
    }

    @Override
    public List<PieFolder> findAllUnsyncedPieFolders() {
        try {
            return pieFilderDBService.findAllUnsyncedPieFolders();
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error findAllUnsynced PieFolders!", ex);
        }
        return new ArrayList<>();
    }

    @Override
    public List<PieFolder> findAllPieFolders() {

        try {
            return pieFilderDBService.findAllPieFolders();
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error find all PieFolders!", ex);
        }
        return new ArrayList<>();
    }

    @Override
    public void resetAllPieFolderSyncedFlags() {
        try {
            pieFilderDBService.resetAllPieFolderSyncedFlags();
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error resetAllPieFolderSyncedFlags!", ex);
        }
    }

    @Override
    public void removePieFolder(PieFolder folder) {
        try {
            pieFilderDBService.removePieFolder(folder);
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error delete pieFolder!", ex);
        }
    }
}
