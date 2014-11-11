/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.configurationService;

import java.io.File;
import java.util.HashMap;
import java.util.Properties;
import org.pieShare.pieShareApp.service.configurationService.api.IApplicationConfigurationService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.propertiesReader.api.IPropertiesReader;
import org.pieShare.pieTools.pieUtilities.service.propertiesReader.exception.NoConfigFoundException;

/**
 *
 * @author Richard
 */
public class ApplicationConfigurationService implements IApplicationConfigurationService {

	private final String HOME_DIR;
	private File BASE_CONFIG_FOLDER;
	private IPropertiesReader configurationReader;
	private File configFile;
	private Properties conf;

	public ApplicationConfigurationService() {
		//ToDo: Config Folder is hard coded. Check if we could do this in an other way.
		HOME_DIR = System.getProperty("user.home");
		BASE_CONFIG_FOLDER = new File(String.format("%s/%s/%s", HOME_DIR, ".pieSystems", ".pieShare"));

		if (!BASE_CONFIG_FOLDER.exists() || !BASE_CONFIG_FOLDER.isDirectory()) {
			BASE_CONFIG_FOLDER.mkdirs();
		}
	}

	@Override
	public File getBaseConfigPath() {
		return this.BASE_CONFIG_FOLDER;
	}

	public void setConfigPath(String folder) {
		BASE_CONFIG_FOLDER = new File(BASE_CONFIG_FOLDER, folder);
		if (!BASE_CONFIG_FOLDER.exists()) {
			BASE_CONFIG_FOLDER.mkdirs();
		}
	}

	public void init() {

		if (configFile == null) {
			this.configFile = new File(BASE_CONFIG_FOLDER, "pieShare.properties");
		}

		if (!configFile.getParentFile().exists()) {
			configFile.getParentFile().mkdirs();
		}

		try {
			//pieShare.properties
			conf = configurationReader.getConfig(configFile);
		}
		catch (NoConfigFoundException ex) {
			PieLogger.error(this.getClass(), "Cannot find pieShareAppConfig.", ex);
		}
	}

	@Override
	public File getDatabaseFolder() {
		File file;

		if (conf == null || !conf.containsKey("databaseDir")) {
			file = new File(BASE_CONFIG_FOLDER, "database");
		}
		else {
			file = new File(conf.getProperty("databaseDir"));
		}

		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

}
