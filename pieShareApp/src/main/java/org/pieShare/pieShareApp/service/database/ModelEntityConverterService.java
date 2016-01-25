/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database;

import org.pieShare.pieShareApp.service.database.api.IModelEntityConverterService;
import java.io.File;
import java.io.IOException;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.entities.ConfigurationEntity;
import org.pieShare.pieShareApp.model.entities.FilterEntity;
import org.pieShare.pieShareApp.model.entities.PieFileEntity;
import org.pieShare.pieShareApp.model.entities.PieFolderEntity;
import org.pieShare.pieShareApp.model.entities.PieUserEntity;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.fileFilterService.filters.RegexFileFilter;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class ModelEntityConverterService implements IModelEntityConverterService {

    private IUserService userService;

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public PieFileEntity convertToEntity(PieFile file) {
        PieFileEntity entity = new PieFileEntity();
        entity.setMd5(file.getMd5());
        entity.setLastModified(file.getLastModified());
        entity.setFileName(file.getName());
        entity.setDeleted(file.isDeleted());
        entity.setId(file.getId());
        return entity;
    }

    @Override
    public PieFile convertFromEntity(PieFileEntity entity) {
        if (entity == null) {
            return null;
        }

        PieFile file = new PieFile();
        file.setName(entity.getFileName());
        file.setLastModified(entity.getLastModified());
        file.setMd5(entity.getMd5());
        file.setId(entity.getId());
        file.setDeleted(entity.isDeleted());
        return file;
    }

    @Override
    public PieUserEntity convertToEntity(PieUser user) {
        PieUserEntity entity = new PieUserEntity();
        entity.setHasPasswordFile(user.hasPasswordFile());
        entity.setUserName(user.getUserName());
        entity.setConfigurationEntity(this.convertToEntity(user.getPieShareConfiguration(), user));
        //entity.getConfigurationEntity().setPieUserEntity(entity);
        return entity;
    }

    @Override
    public PieUser convertFromEntity(PieUserEntity entity) {
        PieUser user = userService.getUser();
        user.setHasPasswordFile(entity.isHasPasswordFile());
        user.setUserName(entity.getUserName());
        user.setIsLoggedIn(false);
        user.setPieShareConfiguration(convertFromEntity(entity.getConfigurationEntity()));
        return user;
    }

    @Override
    public PieShareConfiguration convertFromEntity(ConfigurationEntity entity) {
        if (entity == null) {
            return null;
        }
        PieShareConfiguration configuration = new PieShareConfiguration();
        configuration.setPwdFile(new File(entity.getPwdFile()));
        configuration.setTmpDir(new File(entity.getTmpDir()));
        configuration.setWorkingDir(new File(entity.getWorkingDir()));
        return configuration;
    }

    @Override
    public ConfigurationEntity convertToEntity(PieShareConfiguration conf, PieUser user) {
        ConfigurationEntity entity = new ConfigurationEntity();
        try {
            entity.setPwdFile(conf.getPwdFile().getCanonicalPath());
            entity.setTmpDir(conf.getTmpDir().getCanonicalPath());
            entity.setWorkingDir(conf.getWorkingDir().getCanonicalPath());
        } catch (IOException ex) {
            PieLogger.error(this.getClass(), "Error get Path", ex);
        }

        entity.setUser(user.getUserName());
        return entity;
    }

    @Override
    public FilterEntity convertToEntity(IFilter filter) {
        //ToDo: Spring
        FilterEntity en = new FilterEntity();
		//TODO FILTER
        //en.setPattern(filter.getPattern());
        return en;
    }

    @Override
    public RegexFileFilter convertFromEntity(FilterEntity entity) {
        RegexFileFilter filter = new RegexFileFilter();
        filter.setPattern(entity.getPattern());
        return filter;
    }

    @Override
    public PieFolderEntity convertToEntity(PieFolder folder) {
        PieFolderEntity entity = new PieFolderEntity();
        entity.setDeleted(folder.isDeleted());
        entity.setFolderName(folder.getName());
        entity.setId(folder.getId());
		entity.setLastModified(folder.getLastModified());
        return entity;
    }

    @Override
    public PieFolder convertFromEntity(PieFolderEntity entity) {
        if (entity == null) {
            return null;
        }

        PieFolder folder = new PieFolder();
        folder.setDeleted(entity.isDeleted());
        folder.setName(entity.getFolderName());
        folder.setId(entity.getId());
		folder.setLastModified(entity.getLastModified());
        return folder;
    }
}
