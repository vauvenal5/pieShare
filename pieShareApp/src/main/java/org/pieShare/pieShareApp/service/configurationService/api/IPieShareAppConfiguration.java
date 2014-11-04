/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.configurationService.api;

import java.io.File;

/**
 *
 * @author richy
 */
public interface IPieShareAppConfiguration {

	File getWorkingDirectory();

	File getTempCopyDirectory();
	
	void setWorkingDir(File workingDir);
	
	void setTempCopyDir(File tempCopyDir);
	
	File getBaseConfigPath();
}
