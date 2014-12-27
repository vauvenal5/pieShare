/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.fileService.fileEncryptionService;

import java.io.File;

/**
 *
 * @author Svetoslav
 */
public interface IFileEncryptionService {
	public void encryptFile(File source, File target);
	public void decryptFile(File source, File target);
}
