/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database.api;

import java.util.ArrayList;
import java.util.List;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;

/**
 *
 * @author Richard
 */
public interface IDatabaseService {

        void setConverterService(IModelEntityConverterService converter);
    
	void persist(PieUser model);

	public ArrayList<PieUser> findAllPieUser();

	void removePieUser(PieUser user);

	void mergePieUser(PieUser user);

	void persistFileFilter(IFilter filter);

//	void persist(PieFile file);
	void removeFileFilter(IFilter filter);

	ArrayList<IFilter> findAllFilters();
	
	PieFile findPieFile(PieFile file);
	
	List<PieFile> findAllUnsyncedPieFiles();
	
	List<PieFile> findAllPieFiles();
	
	void resetAllPieFileSynchedFlags();
	
	void mergePieFile(PieFile file);
	
	void persistPieFile(PieFile file);
        
        //when a folder is added the first time
        void persistPieFolder(PieFolder folder);
        
        //update an already added folder
        void mergePieFolder(PieFolder folder);
        
        PieFolder findPieFolder(PieFolder folder);
        
        List<PieFolder> findAllUnsyncedPieFolders();
        
        List<PieFolder> getAllPieFolders();
        
        void resetAllPieFolderSyncedFlags();
        
        
        
}
