/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.configurationService;

import java.io.File;
import java.nio.file.Path;
import java.util.Properties;
import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieTools.pieUtilities.service.propertiesReader.api.IPropertiesReader;
import org.pieShare.pieTools.pieUtilities.service.propertiesReader.exception.NoConfigFoundException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author richy
 */
public class PieShareAppConfiguration implements IPieShareAppConfiguration {

	private IPropertiesReader configurationReader;
	private final String HOME_DIR;
	private Properties conf;
	private final File BASE_CONFIG_FOLDER;
	private File configFile;
	private File workingDir = null;
	private File tempDir = null;

	public PieShareAppConfiguration() {
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

	public void setConfigurationReader(IPropertiesReader configurationReader) {
		this.configurationReader = configurationReader;
	}

	public void init() {

		if (configFile == null) {
			this.configFile = new File(BASE_CONFIG_FOLDER, "pieShare.properties");
		}

		try {
			//pieShare.properties
			conf = configurationReader.getConfig(configFile);
		}
		catch (NoConfigFoundException ex) {
			PieLogger.error(this.getClass(), "Cannot find pieShareAppConfig.", ex);
		}
	}

	public void setConfigPath(String path) {
		configFile = new File(BASE_CONFIG_FOLDER, path);
	}

	@Override
	public File getWorkingDirectory() {
		readWorkingDir();
		return workingDir;
	}

	@Override
	public void setWorkingDir(File workingDir) {
		addProperty("workingDir", workingDir.toPath().toString());
	}

	private void readWorkingDir() {
		String name = "";
		if (conf == null || !conf.containsKey("workingDir")) {
			name = "workingDir";
		}
		else {
			name = conf.getProperty("workingDir");
		}

		File watchDir = new File(name);

		if (!watchDir.exists()) {
			watchDir.mkdirs();
		}

		workingDir = new File(watchDir.getAbsolutePath());
	}

	@Override
	public File getTempCopyDirectory() {
		readTempCopyDir();
		return tempDir;
	}

	private void readTempCopyDir() {
		String name;
		if (conf == null || !conf.containsKey("tempCopyDir")) {
			name = "tempDir";
		}
		else {
			name = conf.getProperty("tempCopyDir");
		}

		File tempCopyDir = new File(name);

		if (!tempCopyDir.exists()) {
			tempCopyDir.mkdirs();
		}

		tempDir = new File(tempCopyDir.getAbsolutePath());
	}

	@Override
	public void setTempCopyDir(File tempCopyDir) {
		addProperty("tempCopyDir", tempCopyDir.toPath().toString());
	}

	private void addProperty(String prop, String value) {
		if (conf == null) {
			conf = new Properties();
		}
		if (conf.contains(prop)) {
			conf.replace(prop, value);
		}
		else {
			conf.put(prop, value);
		}
		configurationReader.saveConfig(conf, configFile);
	}
}
