/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database;

import java.io.File;
import javax.persistence.EntityManager;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.entities.ConfigurationEntity;
import org.pieShare.pieShareApp.model.entities.FilterEntity;
import org.pieShare.pieShareApp.model.entities.PieFileEntity;
import org.pieShare.pieShareApp.model.entities.PieUserEntity;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.configurationService.PieShareConfiguration;
import org.pieShare.pieShareApp.service.database.api.IModelEntityConverterService;
import org.pieShare.pieShareApp.service.fileFilterService.filters.RegexFileFilter;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 *
 * @author Svetoslav
 */
public class ModelEntityConverterService implements IModelEntityConverterService {

	private IBeanService beanService;

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
		return entity;
	}

	public PieUserEntity convertToEntity(PieUser user) {
		PieUserEntity entity = this.beanService.getBean(PieUserEntity.class);
		//todo: fill this
		return entity;
	}

	@Override
	public PieFile convertFromEntity(PieFileEntity entity) {
		PieFile file = this.beanService.getBean(PieFile.class);
		file.setFileName(entity.getFileName());
		file.setLastModified(entity.getLastModified());
		file.setMd5(entity.getMd5());
		file.setRelativeFilePath(entity.getRelativeFilePath());
		return file;
	}

	@Override
	public PieUserEntity userToEntity(PieUser user) {
		PieUserEntity entity = new PieUserEntity();
		entity.setHasPasswordFile(user.hasPasswordFile());;
		entity.setUserName(user.getUserName());
		entity.setConfigurationEntity(this.confToConfEntity(user.getPieShareConfiguration()));
		entity.getConfigurationEntity().setPieUserEntity(entity);
		return entity;
	}

	@Override
	public PieUser entityToUser(PieUserEntity entity) {
		PieUser user = beanService.getBean(PieUser.class);
		user.setHasPasswordFile(entity.isHasPasswordFile());
		user.setUserName(entity.getUserName());
		user.setIsLoggedIn(false);
		user.setPieShareConfiguration(confEntityToConf(entity.getConfigurationEntity()));
		return user;
	}

	@Override
	public PieShareConfiguration confEntityToConf(ConfigurationEntity entity) {
		if (entity == null) {
			return null;
		}
		PieShareConfiguration configuration = new PieShareConfiguration();
		configuration.setPwdFile(new File(entity.getPwdFile()));
		configuration.setTmpDir(new File(entity.getTmpDir()));
		configuration.setWorkingDir(new File(entity.getWorkingDir()));
		configuration.setUser(entity.getUser());
		return configuration;
	}

	@Override
	public ConfigurationEntity confToConfEntity(PieShareConfiguration conf) {
		ConfigurationEntity entity = new ConfigurationEntity();
		entity.setPwdFile(conf.getPwdFile().toPath().toString());
		entity.setTmpDir(conf.getTmpDir().toPath().toString());
		entity.setWorkingDir(conf.getWorkingDir().toPath().toString());
		entity.setUser(conf.getUser());
		return entity;
	}
	
	@Override
	public FilterEntity filterToFilterEntity(IFilter filter) {
		//ToDo: Spring
		FilterEntity en = new FilterEntity();
		en.setPattern(filter.getPattern());
		filter.setEntity(en);
		return en;
	}

	@Override
	public RegexFileFilter filterToFilterEntity(FilterEntity entity) {
		//ToDo: Spring
		RegexFileFilter reg = new RegexFileFilter();
		reg.setEntity(entity);
		reg.setPattern(entity.getPattern());
		return reg;
	}
	
}
