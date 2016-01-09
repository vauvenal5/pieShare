/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.folderService;

import java.io.File;
import java.io.IOException;
import javax.inject.Provider;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.pieFilder.PieFilder;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareConfiguration;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;

/**
 *
 * @author daniela
 */
public abstract class FilderServiceBase implements IFilderService{
    	protected IPieShareConfiguration configuration;
	protected IUserService userService;
        //private Provider<PieFile> pieFileProvider;
        //private Provider<PieFolder> pieFolderProvider;
        //private IHashService hashService;

	/*public void setHashService(IHashService hashService) {
		this.hashService = hashService;
	}
        
        public void setPieFileProvider(Provider<PieFile> pieFileProvider) {
		this.pieFileProvider = pieFileProvider;
	}*/
        
	/*public void setPieFolderProvider(Provider<PieFolder> pieFolderProvider) {
		this.pieFolderProvider = pieFolderProvider;
	}*/
        
        public void setUserService(IUserService userService) {
		this.userService = userService;
	}
        
	public void init() {
		PieUser user = userService.getUser();
		this.configuration = user.getPieShareConfiguration();
	}
        
        @Override
	public void deleteRecursive(PieFilder filder) {
            PieLogger.trace(this.getClass(), "Recursively deleting {}", filder.getRelativePath());
            File localFile = this.getAbsolutePath(filder);
            try {
		if (localFile.isDirectory()) {
			FileUtils.deleteDirectory(localFile);
		} else {
			localFile.delete();
		}
            } catch (IOException ex) {
		PieLogger.error(this.getClass(), "Deleting failed!", ex);
            }
	}
        
        @Override
	public String relativizeFilePath(File file) {
		try {
			String pathBase = configuration.getWorkingDir().getCanonicalFile().toString();
			String pathAbsolute = file.getCanonicalFile().toString();
                        String relative = new File(pathBase).toURI().relativize(new File(pathAbsolute).toURI()).getPath();
			return relative;
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Error in creating relativ file path!", ex);
		}
		return null;
	}

	@Override
	public File getAbsolutePath(PieFilder filder) {
		return new File(configuration.getWorkingDir(), filder.getRelativePath());

	}

	@Override
	public File getAbsoluteTmpPath(PieFilder filder) {
		return new File(configuration.getTmpDir(), filder.getRelativePath());
		
	}
}
