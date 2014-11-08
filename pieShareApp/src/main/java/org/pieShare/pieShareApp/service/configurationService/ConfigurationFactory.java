/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.configurationService;

import java.io.File;
import org.pieShare.pieShareApp.model.entities.ConfigurationEntity;
import org.pieShare.pieShareApp.service.configurationService.api.IApplicationConfigurationService;
import org.pieShare.pieShareApp.service.configurationService.api.IConfigurationFactory;

/**
 *
 * @author Richard
 */
public class ConfigurationFactory implements IConfigurationFactory {

	private IApplicationConfigurationService configurationService;

	public void setApplicationConfiguration(IApplicationConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public PieShareConfiguration createConfig() {
		return confEntityToConf(new ConfigurationEntity());
	}

	@Override
	public void checkAndCreateFolders(PieShareConfiguration conf) {

		if (!conf.getPwdFile().exists()) {
			conf.getPwdFile().getParentFile().mkdirs();
		}

		if (!conf.getTmpDir().exists()) {
			conf.getTmpDir().mkdirs();
		}

		if (!conf.getWorkingDir().exists()) {
			conf.getWorkingDir().mkdirs();
		}
	}

	@Override
	public PieShareConfiguration confEntityToConf(ConfigurationEntity entity) {
		entity = nullCheck(entity);

		PieShareConfiguration configuration = new PieShareConfiguration();
		configuration.setPwdFile(new File(entity.getPwdFile()));
		configuration.setTmpDir(new File(entity.getTmpDir()));
		configuration.setWorkingDir(new File(entity.getWorkingDir()));
		return configuration;
	}

	@Override
	public ConfigurationEntity confToConfEntity(PieShareConfiguration conf) {
		ConfigurationEntity entity = new ConfigurationEntity();
		entity.setPwdFile(conf.getPwdFile().toPath().toString());
		entity.setTmpDir(conf.getTmpDir().toPath().toString());
		entity.setWorkingDir(conf.getWorkingDir().toPath().toString());
		return entity;
	}

	private ConfigurationEntity nullCheck(ConfigurationEntity entity) {
		if (entity.getPwdFile() == null) {
			entity.setPwdFile(String.format("%s/%s", configurationService.getBaseConfigPath(), "pwd.pie"));
		}

		if (entity.getTmpDir() == null) {
			entity.setTmpDir("tmpDir");
		}

		if (entity.getWorkingDir() == null) {
			entity.setWorkingDir("workingDir");
		}

		return entity;
	}

}
