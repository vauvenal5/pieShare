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
public interface IComparerService {
	/**
	 * Compares a given pieFile with the corresponding local pieFile.
	 * Returns 0 if equal, 1 if the given file is newer, -1 if the given file is older.
	 * @param pieFile
	 * @return
	 * @throws IOException
	 * @throws FileConflictException 
	 */
	public int comparePieFile(PieFile pieFile) throws IOException, FileConflictException;
}
