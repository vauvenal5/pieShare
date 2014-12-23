/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.fileService.fileEncryptionService;

import org.pieShare.pieShareApp.model.pieFile.PieFile;

/**
 *
 * @author Svetoslav
 */
public interface IFileEncryptionService {
	public PieFile encryptFile(PieFile file);
	public PieFile decryptFile(PieFile file);
}
