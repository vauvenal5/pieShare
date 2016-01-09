/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.configurationService.api.IPieShareConfiguration;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.api.IFileWatcherService;
import org.pieShare.pieShareApp.service.folderService.FilderServiceBase;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public abstract class FileServiceBase extends FilderServiceBase implements IFileService {
	//protected IPieShareConfiguration configuration;
	protected IFileWatcherService fileWatcherService;
	//protected IUserService userService;

	public void setFileWatcherService(IFileWatcherService fileWatcherService) {
		this.fileWatcherService = fileWatcherService;
	}
	
	/*
        public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	public void init() {
		PieUser user = userService.getUser();
		this.configuration = user.getPieShareConfiguration();
	}
        */

	@Override
	public void waitUntilCopyFinished(File file) {
		FileInputStream st;
		boolean isCopying = true;

		while (isCopying) {
			if(isBeingUsed(file)) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					//nothing needed to do here
					isCopying = false;
				}
			} else {
				isCopying = false;
			}

//			try {
//				Thread.sleep(2000);
//				st = new FileInputStream(file);
//				st.close();
//				isCopying = false;
//			} catch (FileNotFoundException ex) {
//				isCopying = false;
//			} catch (Exception ex) {
//				//nothing needed to do here
//			}
		}
	}
	
	@Override
	public boolean isBeingUsed(File file) {
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.close();
			return false;
		} catch (IOException ex) {
			PieLogger.trace(this.getClass(), "File {} still in use!", file.getName());
		}
		return true;
	}
/*
	@Override
	public void deleteRecursive(PieFile file) {
                PieLogger.trace(this.getClass(), "Recursively deleting {}", file.getRelativePath());
		File localFile = this.getAbsolutePath(file);
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
*/
	@Override
	public void setCorrectModificationDate(PieFile file) {
		File targetFile = this.getAbsolutePath(file);

		//this is no more neccessary due to the event folding service
		//this.fileWatcherService.addPieFileToModifiedList(file);
		if (setCorrectModificationDate(file, targetFile)) {
			this.fileWatcherService.removePieFileFromModifiedList(file);
		}
	}

	@Override
	public void setCorrectModificationDateOnTmpFile(PieFile file) {
		File tmpFile = this.getAbsoluteTmpPath(file);

		setCorrectModificationDate(file, tmpFile);
	}

	private boolean setCorrectModificationDate(PieFile pieFile, File file) {
		PieLogger.trace(this.getClass(), "Date modified {} of {}", pieFile.getLastModified(), pieFile.getRelativePath());

		if (!file.setLastModified(pieFile.getLastModified())) {
			PieLogger.warn(this.getClass(), "Could not set LastModificationDate: {}", file.getAbsolutePath());
			return false;
		}

		return true;
	}
/*
	@Override
	public String relitivizeFilePath(File file) {
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
	public File getAbsolutePath(PieFile file) {
		return new File(configuration.getWorkingDir(), file.getRelativePath());

	}

	@Override
	public File getAbsoluteTmpPath(PieFile file) {
		return new File(configuration.getTmpDir(), file.getRelativePath());
		
	}
        */

	@Override
	public PieFile getPieFile(String relativeFilePath) throws IOException {
		PieUser user = userService.getUser();
		File localFile = new File(user.getPieShareConfiguration().getWorkingDir(), relativeFilePath);
		return this.getPieFile(localFile);
	}

	@Override
	public PieFile getTmpPieFile(PieFile file) throws IOException {
		File tmpFile = new File(this.configuration.getTmpDir(), file.getRelativePath());
		return this.getPieFile(tmpFile);
	}

	@Override
	public PieFile getWorkingPieFile(PieFile file) throws IOException {
		return this.getPieFile(file.getRelativePath());
	}
}
