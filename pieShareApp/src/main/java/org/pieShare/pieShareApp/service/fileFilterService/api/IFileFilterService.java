/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileFilterService.api;

import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;
import java.util.ArrayList;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;
import org.pieShare.pieShareApp.service.userService.IUserService;

/**
 *
 * @author Richard
 */
public interface IFileFilterService {

	void setDatabaseService(IDatabaseService databaseService);
	
	void addFilter(IFilter filer);

	void removeFilter(IFilter filer);

	/**
	 * Check if a PieFile sync is not blocked by a filter
	 *
	 * @param file to check
	 * @return true if sync is allowed, false otherwise
	 */
	boolean checkFile(PieFile file);
	
	/**
	 * Check if a PieFolder sync is not blocked by a filter
	 *
	 * @param file to check
	 * @return true if sync is allowed, false otherwise
	 */
	boolean checkFile(PieFolder file);

	ArrayList<IFilter> getAllFilters();

}
