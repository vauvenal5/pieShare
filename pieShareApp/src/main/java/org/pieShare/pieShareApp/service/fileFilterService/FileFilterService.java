/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileFilterService;

import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;
import java.io.File;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.service.fileFilterService.api.*;
import org.pieShare.pieShareApp.model.pieFile.PieFile;

/**
 *
 * @author Richard
 */
public class FileFilterService implements IFileFilterService {

	private IDatabaseService databaseService;
	private final ArrayList<IFilter> filters;

	public FileFilterService() {
		filters = new ArrayList<>();
	}

	@PostConstruct
	public void init() {
		refreshFilterList();
	}

	@Override
	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	@Override
	public synchronized void addFilter(IFilter filer) {
		databaseService.persistFileFilter(filer);
		refreshFilterList();
	}

	@Override
	public synchronized void removeFilter(IFilter filer) {
		databaseService.removeFileFilter(filer);
		refreshFilterList();
	}

	@Override
	public synchronized boolean checkFile(PieFile file) {
		for (IFilter f : filters) {
			if (f.matches(file.getRelativeFilePath())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public synchronized boolean checkFile(File file) {
		for (IFilter f : filters) {
			if (f.matches(file.toPath().toString())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public synchronized ArrayList<IFilter> getAllFilters() {
		ArrayList<IFilter> ff = new ArrayList<>();
		ff.addAll(filters);

		return ff;
	}

	public synchronized void refreshFilterList() {
		filters.clear();
		filters.addAll(databaseService.findAllFilters());
	}

}
