/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.configurationService;

import java.io.File;
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
	private PieLogger logger = new PieLogger(PieShareAppConfiguration.class);

	public void setConfigurationReader(IConfigurationReader configurationReader) {
		this.configurationReader = configurationReader;
		try {
			//pieShare.properties
			conf = configurationReader.getConfig("/.pieShare/pieShare.properties");
		} catch (NoConfigFoundException ex) {
			logger.error("Cannot find pieShareAppConfig. Message: " + ex.getMessage());
		}
	}

	@Override
	public File getWorkingDirectory() {
		String name = "";
		if (conf == null || !conf.contains("workingDir")) {
			name = "workingDir";
		} else {	
		name = conf.getProperty("workingDir");
		}

		File watchDir = new File(name);

		if (!watchDir.exists()) {
			watchDir.mkdirs();
		}

		return new File(watchDir.getAbsolutePath());
	}

	@Override
	public int getFileSendBufferSize() {
		int defaultSize = 2048;

		if (conf == null || !conf.contains("fileSendBufferSize")) {
			return defaultSize;
		}

		try {
			return Integer.parseInt(conf.getProperty("fileSendBufferSize"));
		} catch (NumberFormatException ex) {
			return defaultSize;
		}
	}

	@Override
	public File getTempCopyDirectory() {
		String name = "";
		if (conf == null || !conf.contains("tempCopyDir")) {
			name = "tempDir";
		} else {
			name = conf.getProperty("workingDir");
		}

		File tempCopyDir = new File(name);

		if (!tempCopyDir.exists()) {
			tempCopyDir.mkdirs();
		}

		return new File(tempCopyDir.getAbsolutePath());
	}
}
