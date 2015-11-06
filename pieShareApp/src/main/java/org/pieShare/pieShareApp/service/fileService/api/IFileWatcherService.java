/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.fileService.api;

import java.io.File;
import java.io.IOException;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;

/**
 *
 * @author Richard
 */
public interface IFileWatcherService extends IShutdownableService {
	void watchDir(File file) throws IOException;
	void addPieFileToModifiedList(PieFile pieFile);
	boolean removePieFileFromModifiedList(PieFile file);
	boolean isPieFileModifiedByUs(PieFile file);
}
