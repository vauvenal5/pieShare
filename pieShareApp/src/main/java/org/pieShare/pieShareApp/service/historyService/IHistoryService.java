/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.historyService;

import java.io.File;
import java.util.List;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;

/**
 *
 * @author Svetoslav
 */
public interface IHistoryService {

	void syncPieFile(PieFile pieFile);
	
	void syncPieFolder(PieFolder pieFolder);
	
	void syncLocalFilders();

	//todo-mr3: talk with chris about this functions an remove them in the long term
		//use instead the relative path function
	@Deprecated
	PieFile getPieFileFromHistory(File file);

	@Deprecated
	PieFolder getPieFolderFromHistory(File file);

	/**
	 * Returns the {@link PieFile} which matches the relative path or null.
	 * 
	 * @param relativePath - The relative path starting from the working directory.
	 * @return the {@link PieFile} or null
	 */
	PieFile getPieFile(String relativePath);
	
	/**
	 * Returns the {@link PieFolder} which matches the relative path or null.
	 * 
	 * @param relativePath - The relative path starting from the working directory.
	 * @return the {@link PieFolder} or null
	 */
	PieFolder getPieFolder(String relativePath);

	/**
	 * This function will return all {@link PieFile} from the DB which match
	 * the given hash.
	 * 
	 * @param hash - The hash for which to search for.
	 * @return list of {@link PieFile} or an empty list
	 */
	List<PieFile> getPieFiles(byte[] hash);

	/**
	 * This function will return all {@link PieFile} from the DB including the
	 * deleted or an empty list.
	 *
	 * @return list of {@link PieFile} or an empty list
	 */
	List<PieFile> getPieFiles();

	/**
	 * This function will return all {@link PieFolder} from the DB including the
	 * deleted or an empty list.
	 *
	 * @return list of {@link PieFolder} or an empty list
	 */
	List<PieFolder> getPieFolders();

}
