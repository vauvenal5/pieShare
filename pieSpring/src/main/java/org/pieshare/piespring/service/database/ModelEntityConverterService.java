/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.piespring.service.database;

import org.pieShare.pieShareApp.service.database.api.IModelEntityConverterService;
import java.io.File;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.entities.api.IConfigurationEntity;
import org.pieShare.pieShareApp.model.entities.api.IFileFilterEntity;
import org.pieShare.pieShareApp.model.entities.api.IPieFileEntity;
import org.pieShare.pieShareApp.model.entities.api.IPieUserEntity;
import org.pieShare.pieShareApp.service.fileFilterService.filters.RegexFileFilter;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieshare.piespring.service.model.entities.ConfigurationEntity;
import org.pieshare.piespring.service.model.entities.FilterEntity;
import org.pieshare.piespring.service.model.entities.PieFileEntity;
import org.pieshare.piespring.service.model.entities.PieUserEntity;

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
	public IPieFileEntity convertToEntity(PieFile file) {
		IPieFileEntity entity = new PieFileEntity();
		entity.setMd5(file.getMd5());
		entity.setLastModified(file.getLastModified());
		entity.setFileName(file.getName());
		entity.setRelativeFilePath(file.getRelativePath());
		entity.setDeleted(file.isDeleted());
		
		entity.setAbsoluteWorkingPath(file.getRelativePath());
		return entity;
	}

	@Override
	public PieFile convertFromEntity(IPieFileEntity entity) {
		if(entity == null) {
			return null;
		}
		
		PieFile file = new PieFile();
		file.setName(entity.getFileName());
		file.setLastModified(entity.getLastModified());
		file.setMd5(entity.getMd5());
		file.setRelativePath(entity.getRelativeFilePath());
		file.setDeleted(entity.isDeleted());
		return file;
	}

	@Override
	public IPieUserEntity convertToEntity(PieUser user) {
		IPieUserEntity entity = new PieUserEntity();
		entity.setHasPasswordFile(user.hasPasswordFile());
		entity.setUserName(user.getUserName());
		entity.setConfigurationEntity(this.convertToEntity(user.getPieShareConfiguration(), user));
		entity.getConfigurationEntity().setPieUserEntity(entity);
		return entity;
	}

	@Override
	public PieUser convertFromEntity(IPieUserEntity entity) {
		PieUser user = userService.getUser();
		user.setHasPasswordFile(entity.isHasPasswordFile());
		user.setUserName(entity.getUserName());
		user.setIsLoggedIn(false);
		user.setPieShareConfiguration(convertFromEntity(entity.getConfigurationEntity()));
		return user;
	}

	@Override
	public PieShareConfiguration convertFromEntity(IConfigurationEntity entity) {
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
	public IConfigurationEntity convertToEntity(PieShareConfiguration conf, PieUser user) {
		ConfigurationEntity entity = new ConfigurationEntity();
		entity.setPwdFile(conf.getPwdFile().toPath().toString());
		entity.setTmpDir(conf.getTmpDir().toPath().toString());
		entity.setWorkingDir(conf.getWorkingDir().toPath().toString());
		entity.setUser(user.getUserName());
		return entity;
	}

	@Override
	public IFileFilterEntity convertToEntity(IFilter filter) {
		//ToDo: Spring
		FilterEntity en = new FilterEntity();
		en.setPattern(filter.getPattern());
		return en;
	}

	@Override
	public RegexFileFilter convertFromEntity(IFileFilterEntity entity) {
		RegexFileFilter filter = new RegexFileFilter();
		filter.setPattern(entity.getPattern());
		return filter;
	}
}
