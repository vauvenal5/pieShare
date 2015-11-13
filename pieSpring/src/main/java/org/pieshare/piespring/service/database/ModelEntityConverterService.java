/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.piespring.service.database;

import java.io.File;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieshare.piespring.model.entities.ConfigurationEntity;
import org.pieshare.piespring.model.entities.FilterEntity;
import org.pieshare.piespring.model.entities.PieFileEntity;
import org.pieshare.piespring.model.entities.PieUserEntity;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieshare.piespring.service.database.IModelEntityConverterService;
import org.pieShare.pieShareApp.service.fileFilterService.filters.RegexFileFilter;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 *
 * @author Svetoslav
 */
public class ModelEntityConverterService implements IModelEntityConverterService {

	private IBeanService beanService;
	private IUserService userService;
	
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	@Override
	public PieFileEntity convertToEntity(PieFile file) {
		PieFileEntity entity = this.beanService.getBean(PieFileEntity.class);
		entity.setMd5(file.getMd5());
		entity.setLastModified(file.getLastModified());
		entity.setFileName(file.getFileName());
		entity.setRelativeFilePath(file.getRelativeFilePath());
		entity.setDeleted(file.isDeleted());
		
		entity.setAbsoluteWorkingPath(file.getRelativeFilePath());
		return entity;
	}

	@Override
	public PieFile convertFromEntity(PieFileEntity entity) {
		if(entity == null) {
			return null;
		}
		
		PieFile file = this.beanService.getBean(PieFile.class);
		file.setFileName(entity.getFileName());
		file.setLastModified(entity.getLastModified());
		file.setMd5(entity.getMd5());
		file.setRelativeFilePath(entity.getRelativeFilePath());
		file.setDeleted(entity.isDeleted());
		return file;
	}

	@Override
	public PieUserEntity convertToEntity(PieUser user) {
		PieUserEntity entity = new PieUserEntity();
		entity.setHasPasswordFile(user.hasPasswordFile());
		entity.setUserName(user.getUserName());
		entity.setConfigurationEntity(this.convertToEntity(user.getPieShareConfiguration(), user));
		entity.getConfigurationEntity().setPieUserEntity(entity);
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
		entity.setPwdFile(conf.getPwdFile().toPath().toString());
		entity.setTmpDir(conf.getTmpDir().toPath().toString());
		entity.setWorkingDir(conf.getWorkingDir().toPath().toString());
		entity.setUser(user.getUserName());
		return entity;
	}

	@Override
	public FilterEntity convertToEntity(IFilter filter) {
		//ToDo: Spring
		FilterEntity en = new FilterEntity();
		en.setPattern(filter.getPattern());
		return en;
	}

	@Override
	public RegexFileFilter convertFromEntity(FilterEntity entity) {
		RegexFileFilter filter = beanService.getBean(RegexFileFilter.class);
		filter.setPattern(entity.getPattern());
		return filter;
	}
}
