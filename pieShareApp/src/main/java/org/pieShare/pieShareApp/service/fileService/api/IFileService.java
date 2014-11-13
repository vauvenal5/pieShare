/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.pieShare.pieShareApp.model.message.FileRequestMessage;
import org.pieShare.pieShareApp.model.message.NewFileMessage;
import org.pieShare.pieShareApp.model.pieFile.PieFile;

/**
 *
 * @author richy
 */
public interface IFileService {
	
	List<PieFile> getAllFiles() throws IOException;

	void deleteRecursive(PieFile file);
	
	void waitUntilCopyFinished(File file);
	
	PieFile getPieFile(File file) throws FileNotFoundException, IOException;
	
    PieFile getPieFile(String filePath) throws FileNotFoundException, IOException;
	
	void setCorrectModificationDate(PieFile file);
	
	Path relitivizeFilePath(File file);
	
	void addPieFileToModifiedList(PieFile pieFile);
	boolean removePieFileFromModifiedList(PieFile file);
}
