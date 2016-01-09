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
 * todo: add folders (filders?) to history service
 */
public interface IHistoryService {
	void syncPieFileWithDb(PieFile pieFile);
	PieFile syncDeleteToHistory(PieFile file);
	List<PieFile> syncLocalPieFilesWithHistory();
        
        /**
         * @param pieFolder 
         */
        void syncPieFolderWithDb(PieFolder pieFolder);
        
        /**
         * @param pieFolder
         * @return 
         */
        PieFolder syncDeletePieFolderToHistory(PieFolder pieFolder);
        
        /**
         * 
         * @return 
         */
        List<PieFolder> syncLocalPieFolderWithHistory();
		
	PieFile getPieFileFromHistory(File file);
	PieFolder getPieFolderFromHistory(File file);
}
