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
    public PieFile findPieFile(PieFile file) {
        try {
            return this.pieFilderDBService.findFile(file.getId());
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Finding one PieFile", ex);
        }
        return null;
    }

    @Override
    public void mergePieFile(PieFile file) {
        /*try {
            PieFileEntity entity = this.modelEntityConverterService.convertToEntity(file);

            if (pieFileDAO.findPieFileById(file.getRelativePath()) != null) {
                pieFileDAO.updatePieFile(entity);
            } else {
                pieFileDAO.savePieFile(entity);
            }
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Updating PieFile", ex);
        }*/

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
        /* // EntityManager em = pieDatabaseManagerFactory.getEntityManger(PieFileEntity.class);
        //  String sqlQuery = String.format("SELECT f FROM %s f WHERE f.synched=TRUE", PieFileEntity.class.getSimpleName());
        // TypedQuery<PieFileEntity> query = em.createQuery(sqlQuery, PieFileEntity.class);

        List<PieFileEntity> qq;
        try {
            qq = pieFileDAO.findAllUnsyncedPieFiles();
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error finding all unsynched PieFiles", ex);
            return null;
        }

        ArrayList<PieFile> files = new ArrayList<>();

        // List<PieFileEntity> entities = query.getResultList();
        for (PieFileEntity entity : qq) {
            files.add(this.modelEntityConverterService.convertFromEntity(entity));
        }

        return files;*/
        return null;
    }

    @Override
    public void resetAllPieFileSynchedFlags() {
        /*try {
            // EntityManager em = pieDatabaseManagerFactory.getEntityManger(PieFileEntity.class);
            ///  String sqlQuery = String.format("UPDATE %s SET synched=TRUE", PieFileEntity.class.getSimpleName());

            pieFileDAO.resetAllPieFileSynchedFlags();
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error reseting all Synched Flags", ex);
        }*/
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
        /* PieFolderEntity entity = this.modelEntityConverterService.convertToEntity(folder);
        if (findPieFolder(folder) != null) {
            try {
                pieFolderDAO.updatePieFolder(entity);
            } catch (SQLException ex) {
                PieLogger.error(this.getClass(), "Error Merging PieFolder!", ex);
            }
        } else {
            persistPieFolder(folder);
        }*/
    }

    @Override
    public PieFolder findPieFolder(PieFolder folder) {
        try {
            return pieFilderDBService.findFolder(folder.getId());
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Finding PieFolder!", ex);
        }
        return null;
    }

    @Override
    public List<PieFolder> findAllUnsyncedPieFolders() {
        /* ArrayList<PieFolder> folders = new ArrayList<>();

        try {
            for (PieFolderEntity entity : pieFolderDAO.findAllUnsyncedPieFolders()) {
                folders.add(this.modelEntityConverterService.convertFromEntity(entity));
            }
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error findAllUnsynced PieFolders!", ex);
        }

        return folders;*/
        return null;
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
        /*  try {
            pieFolderDAO.resetAllPieFolderSynchedFlags();
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error resetAllPieFolderSyncedFlags!", ex);
        }*/
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
