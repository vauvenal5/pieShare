/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.message.FileListRequestMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareConfiguration;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.fileListenerService.api.IFileWatcherService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterAddedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterAddedListener;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import sun.nio.ch.FileChannelImpl;

/**
 *
 * @author Svetoslav
 */
public abstract class FileServiceBase implements IFileService {
	
	protected IBeanService beanService;
	protected IPieShareConfiguration configuration;
	protected IFileWatcherService fileWatcherService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setFileWatcherService(IFileWatcherService fileWatcherService) {
		this.fileWatcherService = fileWatcherService;
	}
	
	public void init() {
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		this.configuration = user.getPieShareConfiguration();
	}
	
	@Override
	public void waitUntilCopyFinished(File file) {
		FileInputStream st;
		boolean isCopying = true;

		while (isCopying) {

			try {
				Thread.sleep(2000);			
				st = new FileInputStream(file);
				st.close();
				isCopying = false;
			}
			catch (FileNotFoundException ex) {
				isCopying = false;
			}
			catch (Exception ex) {
				//nothing needed to do here
			}
		}
	}
	
	@Override
	public void deleteRecursive(PieFile file) {
		File localFile = this.getAbsolutePath(file).toFile();
		try {
			if (localFile.isDirectory()) {
				FileUtils.deleteDirectory(localFile);
			}
			else {
				localFile.delete();
			}
		}
		catch (IOException ex) {
			PieLogger.error(this.getClass(), "Deleting failed!", ex);
		}
	}
	
	@Override
	public void setCorrectModificationDate(PieFile file) {
		File targetFile = this.getAbsolutePath(file).toFile();

		this.fileWatcherService.addPieFileToModifiedList(file);
		if (setCorrectModificationDate(file, targetFile)) {
			this.fileWatcherService.removePieFileFromModifiedList(file);
		}
	}
	
	@Override
	public void setCorrectModificationDateOnTmpFile(PieFile file) {
		File tmpFile = this.getAbsoluteTmpPath(file).toFile();
		
		setCorrectModificationDate(file, tmpFile);
	}
	
	private boolean setCorrectModificationDate(PieFile pieFile, File file) {
		PieLogger.trace(this.getClass(), "Date modified {} of {}", pieFile.getLastModified(), pieFile.getRelativeFilePath());
		
		if (!file.setLastModified(pieFile.getLastModified())) {
			PieLogger.warn(this.getClass(), "Could not set LastModificationDate: {}", file.getAbsolutePath());
			return false;
		}
		
		return true;
	}
	
	@Override
	public Path relitivizeFilePath(File file) {
		Path pathBase = configuration.getWorkingDir().getAbsoluteFile().toPath();
		Path pathAbsolute = file.getAbsoluteFile().toPath();
		return pathBase.relativize(pathAbsolute);
	}
	
	@Override
	public Path getAbsolutePath(PieFile file) {
		File localFile = new File(configuration.getWorkingDir(), file.getRelativeFilePath());
		return localFile.toPath();
	}
	
	@Override
	public Path getAbsoluteTmpPath(PieFile file) {
		File localFile = new File(configuration.getTmpDir(), file.getRelativeFilePath());
		return localFile.toPath();
	}
	
	@Override
	public PieFile getPieFile(String relativeFilePath) throws IOException {
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		File localFile = new File(user.getPieShareConfiguration().getWorkingDir(), relativeFilePath);
		return this.getPieFile(localFile);
	}
	
	@Override
	public PieFile getTmpPieFile(PieFile file) throws IOException {
		File tmpFile = new File(this.configuration.getTmpDir(), file.getRelativeFilePath());
		return this.getPieFile(tmpFile);
	}
	
	@Override
	public PieFile getWorkingPieFile(PieFile file)  throws IOException {
		return this.getPieFile(file.getRelativeFilePath());
	}
}
