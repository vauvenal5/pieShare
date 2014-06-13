/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.configurationReader.api;

import java.util.Properties;
import org.pieShare.pieTools.pieUtilities.service.configurationReader.exception.NoConfigFoundException;

/**
 *
 * @author richy
 */
public interface IConfigurationReader {

	public Properties getConfig(String pathToConfig) throws NoConfigFoundException;
}
