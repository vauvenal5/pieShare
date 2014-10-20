/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileFilterService;

import java.io.File;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.service.fileFilterService.api.*;

/**
 *
 * @author Richard
 */
public class FileFilterService implements IFileFilterService {

	private IDatabaseService databaseService;

	@Override
	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	@Override
	public void addFilter(IFilter filer) {
		databaseService.persistFileFilter(filer);
	}

	@Override
	public void removeFilter(IFilter filer) {
		databaseService.removeFileFilter(filer);
	}

	@Override
	public boolean checkFile(File file) {

		for (IFilter f : databaseService.findAllFilters()) {
			if (f.matches(file.toPath().toString())) {
				return false;
			}
		}
		return true;
	}

}
