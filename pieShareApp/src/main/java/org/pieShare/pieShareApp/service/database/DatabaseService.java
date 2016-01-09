/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.model.entities.FilterEntity;
import org.pieShare.pieShareApp.model.model.entities.PieFileEntity;
import org.pieShare.pieShareApp.model.model.entities.PieUserEntity;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.database.DAOs.FileFilterDAO;
import org.pieShare.pieShareApp.service.database.DAOs.PieFileDAO;
import org.pieShare.pieShareApp.service.database.DAOs.PieUserDAO;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.service.database.api.IModelEntityConverterService;
import org.pieShare.pieShareApp.service.database.api.IPieDatabaseManagerFactory;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author RicLeo00
 */
public class DatabaseService implements IDatabaseService {

    private IModelEntityConverterService modelEntityConverterService;
    private IPieDatabaseManagerFactory pieDatabaseManagerFactory;

    private PieUserDAO pieUserDAO;
    private FileFilterDAO fileFilterDAO;
    private PieFileDAO pieFileDAO;

    public void setPieUserDAO(PieUserDAO pieUserDAO) {
        this.pieUserDAO = pieUserDAO;
    }

    public void setFileFilterDAO(FileFilterDAO fileFilterDAO) {
        this.fileFilterDAO = fileFilterDAO;
    }

    public void setPieFileDAO(PieFileDAO pieFileDAO) {
        this.pieFileDAO = pieFileDAO;
    }

    public DatabaseService() {
    }

    public void setPieDatabaseManagerFactory(IPieDatabaseManagerFactory factory) {
        this.pieDatabaseManagerFactory = factory;
    }

    @Override
    public void setConverterService(IModelEntityConverterService converter) {
        this.modelEntityConverterService = converter;
    }

    @Override
    public void persistPieUser(PieUser model) {
        try {
            pieUserDAO.savePieUser((PieUserEntity) modelEntityConverterService.convertToEntity(model));
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
        ent = (PieUserEntity) modelEntityConverterService.convertToEntity(user);
        try {
            pieUserDAO.deletePieUser(ent);
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Deleting PieUser", ex);
        }
    }

    @Override
    public void mergePieUser(PieUser user) {
        PieUserEntity entity;
        entity = (PieUserEntity) modelEntityConverterService.convertToEntity(user);
        try {
            pieUserDAO.updatePieUser(entity);
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

    /*  public void persist(PieFile file) {
        PieFileEntity entity;
        entity = (PieFileEntity) this.modelEntityConverterService.convertToEntity(file);
        try {
            pieFileDAO.savePieFile(entity);
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Persisting PieFile", ex);
        }
    }*/
    @Override
    public PieFile findPieFile(PieFile file) {
        try {
            return this.modelEntityConverterService.convertFromEntity(pieFileDAO.findPieFileById(file.getRelativePath()));
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Finding one PieFile", ex);
        }
        return null;
    }

    @Override
    public void mergePieFile(PieFile file) {
        try {
            PieFileEntity entity = (PieFileEntity) this.modelEntityConverterService.convertToEntity(file);

            if (pieFileDAO.findPieFileById(file.getRelativePath()) != null) {
                pieFileDAO.updatePieFile(entity);
            }
            else
            {
                pieFileDAO.savePieFile(entity);
            }
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Updating PieFile", ex);
        }
    }

    @Override
    public void persistPieFile(PieFile file) {
        PieFileEntity entity;
        entity = (PieFileEntity) this.modelEntityConverterService.convertToEntity(file);
        try {
            pieFileDAO.savePieFile(entity);
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error Persisting PieFile", ex);
        }
    }

    @Override
    public List<PieFile> findAllUnsyncedPieFiles() {
        // EntityManager em = pieDatabaseManagerFactory.getEntityManger(PieFileEntity.class);
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

        return files;
    }

    @Override
    public void resetAllPieFileSynchedFlags() {
        try {
            // EntityManager em = pieDatabaseManagerFactory.getEntityManger(PieFileEntity.class);
            ///  String sqlQuery = String.format("UPDATE %s SET synched=TRUE", PieFileEntity.class.getSimpleName());

            pieFileDAO.resetAllPieFileSynchedFlags();
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error reseting all Synched Flags", ex);
        }
    }

    @Override
    public List<PieFile> findAllPieFiles() {
        ArrayList<PieFile> files = new ArrayList<>();

        try {
            for (PieFileEntity entity : pieFileDAO.findAllPieFiles()) {
                files.add(this.modelEntityConverterService.convertFromEntity(entity));
            }
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error finding all PieFiles!", ex);

        }
        return files;
    }
}
