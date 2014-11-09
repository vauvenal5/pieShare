/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.configurationService;

import java.io.File;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.entities.ConfigurationEntity;
import org.pieShare.pieShareApp.service.configurationService.api.IApplicationConfigurationService;
import org.pieShare.pieShareApp.service.configurationService.api.IConfigurationFactory;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 *
 * @author Richard
 */
public class ConfigurationFactory implements IConfigurationFactory {

	private IApplicationConfigurationService configurationService;
	private IBeanService beanService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setApplicationConfiguration(IApplicationConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public PieShareConfiguration checkAndCreateConfig(PieShareConfiguration config) {
		if (config == null) {
			config = beanService.getBean(PieShareConfiguration.class);
		}
		checkAndCreateFolders(config);
		return config;
	}

	
	private void checkAndCreateFolders(PieShareConfiguration conf) {

		if (conf.getPwdFile() == null) {
			conf.setPwdFile(new File(String.format("%s/%s", configurationService.getBaseConfigPath(), "pwd.pie")));
		}

		if (!conf.getPwdFile().exists()) {
			conf.getPwdFile().getParentFile().mkdirs();
		}

		if (conf.getTmpDir() == null) {
			conf.setTmpDir(new File("tmpDir"));
		}

		if (!conf.getTmpDir().exists()) {
			conf.getTmpDir().mkdirs();
		}

		if (conf.getWorkingDir() == null) {
			conf.setWorkingDir(new File("workingDir"));
		}

		if (!conf.getWorkingDir().exists()) {
			conf.getWorkingDir().mkdirs();
		}
	}

}
