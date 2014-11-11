/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.historyService;

import java.io.IOException;
import java.util.List;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;

/**
 *
 * @author Svetoslav
 */
public class HistoryService implements IHistoryService {
	
	private IDatabaseService databaseService;
	private IFileService fileService;

	@Override
	public void syncPieFileWithDb(PieFile pieFile) {
		//PieFile dbFile = databaseService.findPieFile(pieFile);
	}
	
	public void syncDeleteToHistory(PieFile file) {
		file.setDeleted(true);
		//todo-history: merge into DB
	}
	
	public void syncLocalPieFilesWithHistory() {
		try {
			//todo: drop files in DB
			
			List<PieFile> files = this.fileService.getAllFiles();
			
			//todo: save files to DB
		} catch (IOException ex) {
		}
	}
	
}
