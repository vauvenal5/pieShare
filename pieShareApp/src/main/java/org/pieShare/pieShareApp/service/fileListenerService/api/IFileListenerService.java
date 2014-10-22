/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.fileListenerService.api;

import org.pieShare.pieShareApp.service.fileService.PieFile;

/**
 *
 * @author Svetoslav
 */
public interface IFileListenerService {
	void addPieFileToModifiedList(PieFile pieFile);
	boolean removePieFileFromModifiedList(PieFile file);
}
