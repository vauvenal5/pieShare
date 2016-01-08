/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.fileService.fileEncryptionService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.IProviderService;

/**
 *
 * @author Svetoslav
 */
public class FileEncryptionService implements IFileEncryptionService {
	
	IProviderService providerService;
	IFileService fileService;
	private IBase64Service base64Service;
	private IUserService userService;

	public void setProviderService(IProviderService providerService) {
		this.providerService = providerService;
	}

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}
	
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}
	
	private Cipher getCipher(int mode) throws InvalidKeyException {
		Cipher cipher = this.providerService.getEnDeCryptCipher();
		PieUser user = userService.getUser();
		cipher.init(mode, user.getPassword().getSecretKey());
		return cipher;
	}
	
	private void rewriteFile(InputStream inStream, OutputStream outStream) throws IOException {
		byte bytes[] = new byte[1024*4];
		int length = 0;		
		while((length = inStream.read(bytes)) != -1) {
			outStream.write(bytes, 0, length);
			//todo: this flush hast probably to be moved outside the while so java can manage the amount of buffered data
			outStream.flush();
		}
		outStream.close();
		inStream.close();
	}
	
	@Override
	public void encryptFile(File source, File target, boolean append) {
		
		try {
			FileInputStream stream = new FileInputStream(source);
			FileOutputStream fileStream = new FileOutputStream(target, append);
			CipherOutputStream outputStream = new CipherOutputStream(fileStream, this.getCipher(Cipher.ENCRYPT_MODE));		
			//Base64OutputStream base64OutStream = new Base64OutputStream(fileStream);
			this.rewriteFile(stream, outputStream);
		} catch (FileNotFoundException ex) {
			PieLogger.error(this.getClass(), "Exception in FileEncrypterService!", ex);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Exception in FileEncrypterService!", ex);
		} catch (InvalidKeyException ex) {
			PieLogger.error(this.getClass(), "Exception in FileEncrypterService!", ex);
		}
	}

	@Override
	public void decryptFile(File source, File target, boolean append) {		
		try {
			//CipherInputStream stream = new CipherInputStream(new FileInputStream(this.fileService.getAbsolutePath(file).toFile()), this.getCipher(Cipher.DECRYPT_MODE));
			FileInputStream fileStream = new FileInputStream(source);
			CipherInputStream stream = new CipherInputStream(fileStream, this.getCipher(Cipher.DECRYPT_MODE));
			//Base64InputStream base64Input = new Base64InputStream(fileStream);
			//FileOutputStream outputStream = new FileOutputStream(this.fileService.getAbsolutePath(workingFile).toFile());
			FileOutputStream outputStream = new FileOutputStream(target, append);
			this.rewriteFile(stream, outputStream);
		} catch (FileNotFoundException ex) {
			PieLogger.error(this.getClass(), "Error!", ex);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Error!", ex);
		} catch (InvalidKeyException ex) {
			PieLogger.error(this.getClass(), "Error!", ex);
		}
	}
}
