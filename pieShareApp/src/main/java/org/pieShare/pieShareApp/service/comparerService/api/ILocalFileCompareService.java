/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.comparerService.api;

import java.io.IOException;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.comparerService.exceptions.FileConflictException;

/**
 * 
 * @author Svetoslav
 * todo make general for Filder?
 */
public interface ILocalFileCompareService {
	
	boolean equalsWithLocalPieFile(PieFile remoteFile);
	
	/**
	 * Compares a given pieFile with the corresponding local pieFile.
	 * Returns 0 if equal, 1 if the given file is newer, -1 if the given file is older.
	 * @param remoteFile
	 * @return
	 * @throws IOException
	 * @throws FileConflictException 
	 */
	int compareToLocalPieFile(PieFile remoteFile);
	
	boolean isConflictedOrNotNeeded(PieFile file);
	
	int compareToLocalPieFolder(PieFolder remoteFolder);
	
	boolean isConflictedOrNotNeeded(PieFolder folder);
}
