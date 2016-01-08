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

        /**
         * Add a PieFolder for the first time into the DB
         * @param folder to add
         */
        void persistPieFolder(PieFolder folder);
        
        /**
         * When a PieFolder is already in the DB but got updated
         * use this method to propagate the update to the DB.
         * @param folder containing the updates
         */
        void mergePieFolder(PieFolder folder);
        
        /**
         * Searches for the given PieFolder in the DB
         * @param folder to search for
         * @return one PieFolder if found, else null
         */
        PieFolder findPieFolder(PieFolder folder);
        
        /**
         * Retrieves all PieFolders with the flag "unsynced" from the DB
         * @return a List of PieFolders which are unsynced
         */
        List<PieFolder> findAllUnsyncedPieFolders();
        
        /**
         * Retrieve all PieFolders which are persisted in the DB
         * @return a List of all PieFolders from the DB
         */
        List<PieFolder> findAllPieFolders();
        
        /**
         * Set for all PieFolders in the DB the value "synced" to TRUE
         */
        void resetAllPieFolderSyncedFlags();
        
        /**
         * Remove a already added PieFolder from the DB
         * @param folder to remove from the DB
         */
        void removePieFolder(PieFolder folder);
        
        
        
}
