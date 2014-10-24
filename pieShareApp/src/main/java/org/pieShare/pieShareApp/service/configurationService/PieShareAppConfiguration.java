/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.configurationService;

import java.io.File;
import java.nio.file.Path;
import java.util.Properties;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareAppConfiguration;
import org.pieShare.pieTools.pieUtilities.service.configurationReader.api.IConfigurationReader;
import org.pieShare.pieTools.pieUtilities.service.configurationReader.exception.NoConfigFoundException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author richy
 */
public class PieShareAppConfiguration implements IPieShareAppConfiguration {

	private IConfigurationReader configurationReader;
	private Properties conf;
	private final String CONFIG_PATH;

	private File workingDir = null;
	private File tempDir = null;

	public PieShareAppConfiguration() {
		this.CONFIG_PATH = "/.pieShare/pieShare.properties";
	}

	public void setConfigurationReader(IConfigurationReader configurationReader) {
		this.configurationReader = configurationReader;
		try {
			//pieShare.properties
			conf = configurationReader.getConfig(CONFIG_PATH);
		} catch (NoConfigFoundException ex) {
			PieLogger.error(this.getClass(), "Cannot find pieShareAppConfig.", ex);
		}
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
		} else {
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
		} else {
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
		} else {
			conf.put(prop, value);
		}
		configurationReader.saveConfig(conf, CONFIG_PATH);
	}
}
