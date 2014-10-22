/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileFilterService.api;

import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;
import java.io.File;
import java.util.ArrayList;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;

/**
 *
 * @author Richard
 */
public interface IFileFilterService {

	void setDatabaseService(IDatabaseService databaseService);

	void addFilter(IFilter filer);

	void removeFilter(IFilter filer);

	boolean checkFile(File file);
	
	ArrayList<IFilter> getAllFilters();

}
