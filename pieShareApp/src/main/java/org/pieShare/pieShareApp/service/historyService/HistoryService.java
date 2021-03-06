/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.historyService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.comparerService.api.ILocalFileCompareService;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;

/**
 *
 * @author Svetoslav
 */
public class HistoryService implements IHistoryService {
	
	private IDatabaseService databaseService;
	private IFileService fileService;

	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}

	@Override
	public void syncPieFileWithDb(PieFile pieFile) {
		databaseService.mergePieFile(pieFile);
	}
	
	@Override
	public PieFile syncDeleteToHistory(PieFile file) {
		PieFile historyFile = this.databaseService.findPieFile(file);
		historyFile.setDeleted(true);
		this.databaseService.mergePieFile(historyFile);
		return historyFile;
	}
	
	@Override
	public List<PieFile> syncLocalPieFilesWithHistory() {
		this.databaseService.resetAllPieFileSynchedFlags();
		List<PieFile> filesToSend = new ArrayList<PieFile>();
		
		try {
			List<PieFile> files = this.fileService.getAllFiles();
			
			for(PieFile file: files) {
				PieFile historyFile = this.databaseService.findPieFile(file);
				
				this.databaseService.mergePieFile(file);
				
				//in this case there is a new file
				if(historyFile == null) {
					filesToSend.add(file);
				} 
				else 
				{
					//in this case a file has changed
					if(!file.equals(historyFile)){
						filesToSend.add(file);
					}
				}
			}
			
			//get all deleted files
			List<PieFile> deletedFiles = this.databaseService.findAllUnsyncedPieFiles();
			filesToSend.addAll(deletedFiles);
		} catch (IOException ex) {
			//todo-history: what has to be done here?
		}
		
		return filesToSend;
	}
	
}
