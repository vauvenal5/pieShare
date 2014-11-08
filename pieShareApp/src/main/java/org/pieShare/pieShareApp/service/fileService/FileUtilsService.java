/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareConfiguration;
import org.pieShare.pieShareApp.service.fileListenerService.api.IFileListenerService;
import org.pieShare.pieShareApp.service.fileService.api.IFileUtilsService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;

/**
 *
 * @author Svetoslav
 */
public class FileUtilsService implements IFileUtilsService {

	private IBeanService beanService;
	private IHashService hashService;
	private IFileListenerService fileListener;
	private IPieShareConfiguration configuration;

	public void setFileListener(IFileListenerService fileListener) {
		this.fileListener = fileListener;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setHashService(IHashService hashService) {
		this.hashService = hashService;
	}

	@PostConstruct
	public void init() {
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		configuration = user.getPieShareConfiguration();
	}

	@Override
	public void setCorrectModificationDate(PieFile file) {
		PieLogger.trace(this.getClass(), "Date modified {} of {}", file.getLastModified(), file.getRelativeFilePath());
		File targetFile = new File(this.configuration.getWorkingDir(), file.getRelativeFilePath());

		this.fileListener.addPieFileToModifiedList(file);
		if (!targetFile.setLastModified(file.getLastModified())) {
			this.fileListener.removePieFileFromModifiedList(file);
			PieLogger.warn(this.getClass(), "Could not set LastModificationDate: {}", file.getRelativeFilePath());
		}
	}

	@Override
	public PieFile getPieFile(File file) throws FileNotFoundException, IOException {
		/*if (!file.exists()) {
		 throw new FileNotFoundException("File: " + file.getPath() + " does not exist");
		 }*/

		PieFile pieFile = beanService.getBean(PieShareAppBeanNames.getPieFileName());

		pieFile.setRelativeFilePath(relitivizeFilePath(file).toString());

		pieFile.setFileName(file.getName());
		pieFile.setLastModified(file.lastModified());

		if (file.exists()) {
			pieFile.setMd5(hashService.hashStream(file));
		}

		return pieFile;
	}

	@Override
	public PieFile getPieFile(String filePath) throws FileNotFoundException, IOException {
		File file = new File(filePath);
		return this.getPieFile(file);
	}

	@Override
	public Path relitivizeFilePath(File file) {
		Path pathBase = configuration.getWorkingDir().toPath();//new File(pieAppConfig.getWorkingDirectory().getAbsolutePath()).toPath();
		Path pathAbsolute = file.toPath(); // Paths.get("/var/data/stuff/xyz.dat");
		return pathBase.relativize(pathAbsolute);
	}

}
