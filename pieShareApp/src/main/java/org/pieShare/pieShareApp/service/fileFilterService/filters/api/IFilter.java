/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileFilterService.filters.api;

import org.pieShare.pieShareApp.model.api.IBaseModel;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.pieFilder.PieFolder;

/**
 *
 * @author Richard
 */
public interface IFilter extends IBaseModel {

	//void setPattern(String pattern);

	//String getPattern();

	/**
	 * Checks filter on given PieFile.
	 * @param file
	 * @return true if the filter matches, else false
	 */
	boolean matches(PieFile file);
	
	/**
	 * Checks filter on given PieFolder.
	 * @param folder
	 * @return true if the filter matches, else false
	 */
	boolean matches(PieFolder folder);
}
