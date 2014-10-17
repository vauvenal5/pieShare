/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.configurationReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyValue;
import org.pieShare.pieTools.pieUtilities.service.configurationReader.api.IConfigurationReader;
import org.pieShare.pieTools.pieUtilities.service.configurationReader.exception.NoConfigFoundException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.springframework.stereotype.Component;

/**
 *
 * @author richy
 */
public class ConfigurationReader implements IConfigurationReader {

	private final PieLogger logger = new PieLogger(ConfigurationReader.class);
	private final String homeDir;
	private final String configSavePath;
	private final File configFolder;

	@Override
	public File getBaseConfigPath() {
		return configFolder;
	}

	public ConfigurationReader() {
		//ToDo: Config Folder is hard coded. Check if we could do this in an other way.
		homeDir = System.getProperty("user.home");
		configSavePath = homeDir + "/" + ".pieSystems/";
		configFolder = new File(configSavePath);

		if (!configFolder.exists() || !configFolder.isDirectory()) {
			configFolder.mkdirs();
		}
	}

	@Override
	public void saveConfig(Properties props, String pathToConfig) {
		File config = new File(configFolder, pathToConfig);
		try {
			if(!config.exists()) config.createNewFile();
			FileOutputStream outStr = new FileOutputStream(config);
			props.store(outStr, "");
			outStr.close();
		} catch (IOException ex) {
			//ToDo: handle
			Logger.getLogger(ConfigurationReader.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public Properties getConfig(String pathToConfig) throws NoConfigFoundException {
		File config = new File(configFolder, pathToConfig);

		if (!config.getParentFile().exists()) {
			config.getParentFile().mkdirs();
		}

		if (!config.exists()) {
			logger.error("Configuration: " + config.getAbsolutePath() + " does not exists.");
			throw new NoConfigFoundException("Configuration: " + config.getAbsolutePath() + " does not exists.");
		}

		Properties prop = new Properties();

		try {
			//load a properties file from class path, inside static method
			FileInputStream inputStr = new FileInputStream(config);
			prop.load(inputStr);
			inputStr.close();
		} catch (IOException ex) {
			logger.error("Error reding configuration");
			throw new NoConfigFoundException(ex.getMessage());

		}
		return prop;
	}
}
