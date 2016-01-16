/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.folderService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Provider;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.pieFilder.PieFilder;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareConfiguration;
import org.pieShare.pieShareApp.service.fileService.api.IFilderIterationCallback;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;

/**
 *
 * @author daniela
 */
public abstract class FilderServiceBase implements IFilderService {

	protected IPieShareConfiguration configuration;
	protected IUserService userService;

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
		deleteRecursive(this.getAbsolutePath(filder));
	}

	@Override
	public void deleteRecursive(File localFile) {
		//todo-dani: this should not fail silently!!! propagate the exception
			//the caller should decide what has to happen!
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
