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
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.service.folderService.IFilderService;

/**
 *
 * @author richy
 */
public interface IFileService extends IFilderService {
	
	List<PieFile> getAllFiles() throws IOException;
	
	void waitUntilCopyFinished(File file);
	
	boolean isBeingUsed(File file);
	
	//todo-FileServie: which is the best way to handle not existing files:
			//return null
			//throw Exception
			//pieFile.exists()
	PieFile getPieFile(File file) throws FileNotFoundException, IOException;
	
    PieFile getPieFile(String fileName) throws FileNotFoundException, IOException;
	
	void setCorrectModificationDate(PieFile file);
	
	void setCorrectModificationDateOnTmpFile(PieFile file);
	
        PieFile getTmpPieFile(PieFile file) throws FileNotFoundException, IOException;
        
        PieFile getWorkingPieFile(PieFile file) throws FileNotFoundException, IOException;

}
