/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.historyService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.service.fileService.api.IFilderIterationCallback;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.fileService.api.IFileUtilitiesService;
import org.pieShare.pieShareApp.service.userService.IUserService;

/**
 *
 * @author Svetoslav
 */
public class HistoryService implements IHistoryService {
	
	private IDatabaseService databaseService;
	private IFileService fileService;
	private IFileUtilitiesService fileUtilitiesService;
	private IUserService userService;

	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	public void setFileUtilitiesService(IFileUtilitiesService fileUtilitiesService) {
		this.fileUtilitiesService = fileUtilitiesService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@Override
	public void syncPieFile(PieFile pieFile) {
		databaseService.mergePieFile(pieFile);
	}
	
	@Override
	public void syncLocalFilders() {
		//todo: is it possible to merge this two to one?
		this.databaseService.resetAllPieFileSynchedFlags();
		this.databaseService.resetAllPieFolderSyncedFlags();
		
		//here we sync the current HDD state with our history
		File parent = this.userService.getUser().getPieShareConfiguration().getWorkingDir();
		this.fileUtilitiesService.walkFilderTree(parent, new IFilderIterationCallback() {
			@Override
			public void handleFile(PieFile file) {
				//todo-mr3: save synced flag for file
				syncPieFile(file);
			}

			@Override
			public void handleFolder(PieFolder folder) {
				//todo-mr3: save synced flag for folder
				syncPieFolder(folder);
			}
		});
		
		//here we now assume that all files that are in our history but not
		//on our HDD have been deleted while we where offline
		//todo-mr3: only load unsynced and not deleted files
		List<PieFile> pieFiles = databaseService.findAllUnsyncedPieFiles();
		for(PieFile file: pieFiles) {
			file.setDeleted(true);
			databaseService.mergePieFile(file);
		}
		
		List<PieFolder> pieFolders = databaseService.findAllUnsyncedPieFolders();
		for(PieFolder folder: pieFolders) {
			folder.setDeleted(true);
			databaseService.mergePieFolder(folder);
		}
		
		//todo-mr3: check all files and folders that are new against the unsyced ones
			//how do you propagate offline moves?!
		//todo-mr3: all unsynced files and folders (after the moved check) have to be marked deleted
	}

	@Override
	@Deprecated
	public PieFile getPieFileFromHistory(File file) {
		PieFile searchFile = new PieFile();
		searchFile.setRelativePath(this.fileService.relativizeFilePath(file));
		return this.databaseService.findPieFile(searchFile);
	}

	@Override
	@Deprecated
	public PieFolder getPieFolderFromHistory(File file) {
		PieFolder searchFolder = new PieFolder();
		searchFolder.setRelativePath(this.fileService.relativizeFilePath(file));
		return this.databaseService.findPieFolder(searchFolder);
	}

	@Override
	public List<PieFile> getPieFiles() {
		return this.databaseService.findAllPieFiles();
	}

	@Override
	public List<PieFolder> getPieFolders() {
		return this.databaseService.findAllPieFolders();
	}

	@Override
	public PieFile getPieFile(String relativePath) {
		//todo-mr3: this has to be refactored!!! 
			//the file is being searched by relative 
			//path and this has to be visible somehow
		PieFile searchFile = new PieFile();
		searchFile.setRelativePath(relativePath);
		return this.databaseService.findPieFile(searchFile);
	}

	@Override
	public List<PieFile> getPieFiles(byte[] hash) {
		return this.databaseService.findPieFileByHash(hash);
	}

	@Override
	public void syncPieFolder(PieFolder pieFolder) {
		this.databaseService.mergePieFolder(pieFolder);
	}

	@Override
	public PieFolder getPieFolder(String relativePath) {
		PieFolder folder = new PieFolder();
		folder.setRelativePath(relativePath);
		return this.databaseService.findPieFolder(folder);
	}
	
}
