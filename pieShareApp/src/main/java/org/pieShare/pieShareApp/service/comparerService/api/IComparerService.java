/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.comparerService.api;

import java.io.IOException;
import java.util.List;
import org.pieShare.pieShareApp.service.comparerService.exceptions.FileConflictException;
import org.pieShare.pieShareApp.service.fileService.PieFile;

/**
 *
 * @author Richard
 */
public interface IComparerService {
	public void comparePieFileList(List<PieFile> list) throws IOException, FileConflictException;
	public boolean isPieFileDesired(PieFile remotePieFile) throws IOException, FileConflictException;
	public void comparePieFile(PieFile pieFile) throws IOException, FileConflictException;
}
