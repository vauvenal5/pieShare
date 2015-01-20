/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.comparerService.api;

import java.io.IOException;
import java.util.List;
import org.pieShare.pieShareApp.service.comparerService.exceptions.FileConflictException;
import org.pieShare.pieShareApp.model.pieFile.PieFile;

/**
 *
 * @author Richard
 */
public interface ICompareService {	
	/**
	 * Returns 0 if both files are equal.
	 * Returns -1 if file2 is newer.
	 * Returns 1 if file1 is newer.
	 * @param file1
	 * @param file2
	 * @return
	 * @throws FileConflictException 
	 */
	public int comparePieFiles(PieFile file1, PieFile file2) throws FileConflictException;
}
