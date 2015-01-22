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
import java.nio.channels.FileLock;
import java.security.InvalidKeyException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.pieShare.pieShareApp.model.PieShareAppBeanNames;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.fileService.api.IFileService;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.IProviderService;
import org.pieShare.pieTools.pieUtilities.service.security.encodeService.api.IEncodeService;
import org.pieShare.pieTools.pieUtilities.service.security.hashService.IHashService;

/**
 *
 * @author Svetoslav
 */
public class FileEncryptionService implements IFileEncryptionService {
	
	IProviderService providerService;
	IFileService fileService;
	IBeanService beanService;
	private IBase64Service base64Service;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setProviderService(IProviderService providerService) {
		this.providerService = providerService;
	}

	public void setFileService(IFileService fileService) {
		this.fileService = fileService;
	}
	
	private Cipher getCipher(int mode) throws InvalidKeyException {
		Cipher cipher = this.providerService.getEnDeCryptCipher();
		PieUser user = beanService.getBean(PieShareAppBeanNames.getPieUser());
		cipher.init(mode, user.getPassword().getSecretKey());
		return cipher;
	}
	
	private void rewriteFile(InputStream inStream, OutputStream outStream) throws IOException {
		byte bytes[] = new byte[1024*4];
		int length = 0;		
		while((length = inStream.read(bytes)) != -1) {
			outStream.write(bytes, 0, length);
			outStream.flush();
		}
		outStream.close();
		inStream.close();
	}
	
	@Override
	public void encryptFile(File source, File target) {
		
		try {
			FileInputStream stream = new FileInputStream(source);
			FileOutputStream fileStream = new FileOutputStream(target);
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
	public void decryptFile(File source, File target) {		
		try {
			//CipherInputStream stream = new CipherInputStream(new FileInputStream(this.fileService.getAbsolutePath(file).toFile()), this.getCipher(Cipher.DECRYPT_MODE));
			FileInputStream fileStream = new FileInputStream(source);
			CipherInputStream stream = new CipherInputStream(fileStream, this.getCipher(Cipher.DECRYPT_MODE));
			//Base64InputStream base64Input = new Base64InputStream(fileStream);
			//FileOutputStream outputStream = new FileOutputStream(this.fileService.getAbsolutePath(workingFile).toFile());
			FileOutputStream outputStream = new FileOutputStream(target);
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
