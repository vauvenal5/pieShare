/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.fileService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;

/**
 *
 * @author Svetoslav
 */
public class HistoryFileService extends FileServiceBase {
	
	private IDatabaseService databaseService;

	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	@Override
	public List<PieFile> getAllFiles() throws IOException {
		return this.databaseService.findAllPieFiles();
	}

	@Override
	public PieFile getPieFile(File file) throws FileNotFoundException, IOException {
		PieFile pieFile = new PieFile();
		pieFile.setFileName(file.getName());
		pieFile.setRelativeFilePath(this.relitivizeFilePath(file).toString());
		PieFile foundFile = this.databaseService.findPieFile(pieFile);
		return foundFile;
	}

	@Override
	public PieFile getPieFile(String filePath) throws FileNotFoundException, IOException {
		return this.getPieFile(new File(filePath));
	}
	
}
