/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.fileService.fileEncryptionService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.IProviderService;
import org.pieShare.pieTools.pieUtilities.service.security.encodeService.api.IEncodeService;

/**
 *
 * @author Svetoslav
 */
public class FileEncryptionService implements IFileEncryptionService {
	
	IProviderService providerService;
	IFileService fileService;

	public void setProviderService(IProviderService providerService) {
		this.providerService = providerService;
	}

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}
	
	private void rewriteFile(InputStream inStream, OutputStream outStream) throws IOException {
		byte bytes[] = new byte[1024];
		int length = 0;
		while((length = inStream.read(bytes)) != -1) {
			outStream.write(bytes, 0, length);
		}
		outStream.flush();
		outStream.close();
		inStream.close();
	}
	
	@Override
	public PieFile encryptFile(PieFile file) {
		PieFile tmpFile = null;
		
		try {
			tmpFile = this.fileService.getTmpPieFile(file);
			FileInputStream stream = new FileInputStream(this.fileService.getAbsolutePath(file).toFile());
			CipherOutputStream outputStream = new CipherOutputStream(new FileOutputStream(this.fileService.getAbsolutePath(tmpFile).toFile()), this.providerService.getEnDeCryptCipher());
			
			this.rewriteFile(stream, outputStream);
		} catch (FileNotFoundException ex) {
			PieLogger.error(this.getClass(), "Exception in FileEncrypterService!", ex);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Exception in FileEncrypterService!", ex);
		}
		
		return tmpFile;
	}

	@Override
	public PieFile decryptFile(PieFile file) {
		PieFile workingFile = null;
		
		try {
			workingFile = this.fileService.getWorkingPieFile(file);
			
			CipherInputStream stream = new CipherInputStream(new FileInputStream(this.fileService.getAbsolutePath(file).toFile()), this.providerService.getEnDeCryptCipher());
			FileOutputStream outputStream = new FileOutputStream(this.fileService.getAbsolutePath(workingFile).toFile());
			
			this.rewriteFile(stream, outputStream);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(FileEncryptionService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(FileEncryptionService.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return workingFile;
	}
}
