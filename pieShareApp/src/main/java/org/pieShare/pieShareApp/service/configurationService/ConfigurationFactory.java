/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.configurationService;

import org.pieShare.pieShareApp.model.PieShareConfiguration;
import java.io.File;
import javax.inject.Provider;
import org.pieShare.pieShareApp.service.configurationService.api.IApplicationConfigurationService;
import org.pieShare.pieShareApp.service.configurationService.api.IConfigurationFactory;
import org.pieShare.pieShareApp.service.configurationService.api.ICustomConfiguration;

/**
 *
 * @author Richard
 */
public class ConfigurationFactory implements IConfigurationFactory {

	private ICustomConfiguration customConfigurationService;
	private IApplicationConfigurationService configurationService;
	private Provider<PieShareConfiguration> pieShareConfigurationProvider;

	public void setPieShareConfigurationProvider(Provider<PieShareConfiguration> pieShareConfigurationProvider) {
		this.pieShareConfigurationProvider = pieShareConfigurationProvider;
	}

	public void setApplicationConfiguration(IApplicationConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
	
	public void setCustomConfigurationService(ICustomConfiguration customConfigurationService) {
		this.customConfigurationService = customConfigurationService;
	}

	@Override
	public PieShareConfiguration checkAndCreateConfig(PieShareConfiguration config, boolean create) {
		if (config == null) {
			config = this.pieShareConfigurationProvider.get();
		}
		if (customConfigurationService != null){
			customConfigurationService.SetCustomSettings(config);
		}
		
		checkAndCreateFolders(config, create);
		return config;
	}

	private void checkAndCreateFolders(PieShareConfiguration conf, boolean createFolders) {

		if (conf.getPwdFile() == null) {
			conf.setPwdFile(new File(String.format("%s/%s", configurationService.getBaseConfigPath(), "pwd.pie")));
		}

		if (conf.getTmpDir() == null) {
			conf.setTmpDir(new File(String.format("%s/%s", configurationService.getBaseConfigPath(), "tmpDir")));
		}

		if (!conf.getTmpDir().exists() && createFolders) {
			conf.getTmpDir().mkdirs();
		}

		if (conf.getWorkingDir() == null) {
                        //todo: important!!! this has to be change to work relatively to the tmpDir!!!
                        //instead of configuring seperate paths for tmp database and working dir setup one path
                        //and relatively to this path all other dirs get set up
			conf.setWorkingDir(new File("PieShare"));
		}

		if (!conf.getWorkingDir().exists() && createFolders) {
			conf.getWorkingDir().mkdirs();
		}
	}
}
