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
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.api.IFileWatcherService;
import org.pieShare.pieShareApp.service.folderService.FilderServiceBase;
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
	
	@Override
	public void waitUntilCopyFinished(File file) {
		if(!file.exists()) {
			return;
		}
		
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

	@Override
	public PieFile getPieFile(String relativeFilePath) throws IOException {
		PieUser user = userService.getUser();
		File localFile = new File(user.getPieShareConfiguration().getWorkingDir(), relativeFilePath);
		if(!localFile.exists()) {
			return null;
		}
		return this.getPieFile(localFile);
	}

//	//todo: apparently unused
//	@Override
//	public PieFile getTmpPieFile(PieFile file) throws IOException {
//		File tmpFile = new File(this.configuration.getTmpDir(), file.getRelativePath());
//		return this.getPieFile(tmpFile);
//	}

//	@Override
//	public PieFile getWorkingPieFile(PieFile file) throws IOException {
//		return this.getPieFile(file.getRelativePath());
//	}
}
