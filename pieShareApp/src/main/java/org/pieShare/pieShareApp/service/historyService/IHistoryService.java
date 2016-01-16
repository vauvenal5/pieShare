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
	void syncPieFileWithDb(PieFile pieFile);
	PieFile syncDeleteToHistory(PieFile file);
        
        /**
         * Sync PieFolder to DB.
         * @param pieFolder Folder to sync.
         */
        void syncPieFolderWithDb(PieFolder pieFolder);
        
        /**
         * Sync the delete from a folder to DB.
         * @param pieFolder deleted folder.
         * @return deleted PieFolder from DB
         */
        PieFolder syncDeletePieFolderToHistory(PieFolder pieFolder);
		
	PieFile getPieFileFromHistory(File file);
	PieFolder getPieFolderFromHistory(File file);
	
	PieFile getPieFileFromHistory(PieFile file);
	
	List<PieFile> getPieFiles();
	
	void syncLocalFilders();
	
	/**
	 * This function will return all {@link PieFolder} from the DB 
	 * including the deleted or an empty list.
	 * 
	 * @return list of {@link PieFolder} or an empty list
	 */
	List<PieFolder> getPieFolders();
}
