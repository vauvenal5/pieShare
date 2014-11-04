/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.propertiesReader.api;

import java.io.File;
import java.util.Properties;
import org.pieShare.pieTools.pieUtilities.service.propertiesReader.exception.NoConfigFoundException;

/**
 *
 * @author richy
 */
public interface IPropertiesReader {

	Properties getConfig(File configFile) throws NoConfigFoundException;
	
	void saveConfig(Properties props, File configFile);
}
