/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.piespring.service;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.service.configurationService.api.IApplicationConfigurationService;
import org.pieShare.pieShareApp.service.database.DatabaseFactory;
import org.pieShare.pieShareApp.service.database.api.IPieDatabaseManagerFactory;
import org.pieshare.piespring.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.propertiesReader.api.IPropertiesReader;
import org.pieShare.pieTools.pieUtilities.service.propertiesReader.exception.NoConfigFoundException;

/**
 *
 * @author Richard
 */
public class ApplicationConfigurationService implements IApplicationConfigurationService {

	protected File BASE_CONFIG_FOLDER;
	private IPropertiesReader propertiesReader;
	private File configFile;
	private Properties conf;
	private IBeanService beanService;

	public ApplicationConfigurationService() {
		//ToDo: Config Folder is hard coded. Check if we could do this in an other way.
		String os = System.getProperty("os.name");
		//try to get the appData folder in windows
		String baseFolder = System.getenv("APPDATA");
		//if on linux fall back to home folder
		if (baseFolder == null || !os.contains("win")) {
			baseFolder = System.getProperty("user.home");
		}
		BASE_CONFIG_FOLDER = new File(String.format("%s/%s/%s", baseFolder, ".pieSystems", ".pieShare"));

		if (!BASE_CONFIG_FOLDER.exists() || !BASE_CONFIG_FOLDER.isDirectory()) {
			BASE_CONFIG_FOLDER.mkdirs();
		}
	}

	@Override
	public File getBaseConfigPath() {
		return this.BASE_CONFIG_FOLDER;
	}

	public void setPropertiesReader(IPropertiesReader propertiesReader) {
		this.propertiesReader = propertiesReader;
	}

	@Override
	public void setDatabaseFolder(File folder) {
		File db = getDatabaseFolder();
		addProperty("databaseDir", folder.toPath().toString());
		//todo-sv: i thing this does not belong here!! if we move this then the class can go back to the pieShareApp package
		IPieDatabaseManagerFactory fact = beanService.getBean(DatabaseFactory.class);
		fact.closeDB();
		try {
			for (File file : db.listFiles()) {
				if (file.isDirectory()) {
					FileUtils.moveDirectory(file, folder);
				} else {
					FileUtils.moveFileToDirectory(file, folder, false);
				}
			}
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Error copy database to new location", ex);
		}

		fact.init();
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	private void addProperty(String key, String value) {
		if (conf == null) {
			conf = new Properties();
		}
		if (!conf.containsKey(key)) {
			conf.put(key, value);
		} else {
                        conf.put(key, value);
			//conf.replace(key, value);
		}
		propertiesReader.saveConfig(conf, configFile);
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
			conf = propertiesReader.getConfig(configFile);
		} catch (NoConfigFoundException ex) {
			PieLogger.error(this.getClass(), "Cannot find pieShareAppConfig.", ex);
		}
	}

	@Override
	public File getDatabaseFolder() {
		File file;

		if (conf == null || !conf.containsKey("databaseDir")) {
			file = new File(BASE_CONFIG_FOLDER, "database");
		} else {
			file = new File(conf.getProperty("databaseDir"));
		}

		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

}
