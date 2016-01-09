/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model;

import java.io.File;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareConfiguration;

/**
 *
 * @author Richard
 */
public class PieShareConfiguration implements IPieShareConfiguration {

    private File workingDir;
    private File tmpDir;
    private File pwdFile;

    @Override
    public File getWorkingDir() {
        return getAndCreateFirst(workingDir);
    }

    @Override
    public void setWorkingDir(File workingDir) {
        this.workingDir = workingDir;
    }

    @Override
    public File getTmpDir() {
        return getAndCreateFirst(tmpDir);
    }

    @Override
    public void setTmpDir(File tmpDir) {
        this.tmpDir = tmpDir;
    }

    @Override
    public File getPwdFile() {
        return getAndCreateFirst(pwdFile);
    }

    @Override
    public void setPwdFile(File pwdFile) {
        this.pwdFile = pwdFile;
    }

	//TODO remove again because it is not needed anymore and didn't work as intended anyways
    private File getAndCreateFirst(File file) {
		if(file == null) {
			return file;
		}
		
        if (file.isDirectory()) {
            if (!file.exists()) {
                file.mkdirs();
            }
        }
		else {
			File parentFile = file.getParentFile();
			if(parentFile != null && !parentFile.exists()) {
				parentFile.mkdirs();
			}
		}
        return file;
    }
}
