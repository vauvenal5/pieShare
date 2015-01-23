/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.propertiesReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.propertiesReader.api.IPropertiesReader;
import org.pieShare.pieTools.pieUtilities.service.propertiesReader.exception.NoConfigFoundException;

/**
 *
 * @author richy
 */
public class PropertiesReader implements IPropertiesReader {
	
	@Override
	public void saveConfig(Properties props, File configFile) {
		try {
			if (!configFile.exists()) {
				configFile.createNewFile();
			}
			FileOutputStream outStr = new FileOutputStream(configFile);
			props.store(outStr, "");
			outStr.close();
		}
		catch (IOException ex) {
			//ToDo: handle
			PieLogger.error(this.getClass(), "Error!", ex);
		}
	}

	@Override
	public Properties getConfig(File configFile) throws NoConfigFoundException {

		if (!configFile.exists()) {
			throw new NoConfigFoundException(String.format("Configuration: %s does not exists.", configFile.getAbsolutePath()));
		}

		Properties prop = new Properties();

		try {
			//load a properties file from class path, inside static method
			FileInputStream inputStr = new FileInputStream(configFile);
			prop.load(inputStr);
			inputStr.close();
		}
		catch (IOException ex) {
			throw new NoConfigFoundException(ex);

		}
		return prop;
	}
}
