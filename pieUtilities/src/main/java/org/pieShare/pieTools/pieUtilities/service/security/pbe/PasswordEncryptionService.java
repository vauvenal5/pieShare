/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.security.pbe;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.IProviderService;

/**
 *
 * @author Svetoslav
 */
public class PasswordEncryptionService implements IPasswordEncryptionService {

	//todo-sv: change this
	public byte[] salt = "changeThis".getBytes();
	public int iterations = 5000;

	private IProviderService providerService;

	public PasswordEncryptionService() {

	}

	public void setProviderService(IProviderService service) {
		this.providerService = service;
	}

        @Override
    public EncryptedPassword getEncryptedPasswordFromExistingSecretKey(byte[] encodedKey) {
                
        SecretKeyFactory keyFactory = providerService.getSecretKeyFactory();
        SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, keyFactory.getAlgorithm());
        
        EncryptedPassword encPwd = new EncryptedPassword();
        encPwd.setPassword(originalKey.getEncoded());
        encPwd.setSecretKey(originalKey);
        return encPwd;
    }
        
	@Override
	public EncryptedPassword encryptPassword(PlainTextPassword plainTextPassword) {
		try {
			PBEKeySpec keySpec = new PBEKeySpec(Arrays.toString(plainTextPassword.password).toCharArray(), salt, iterations);
			//this does not ensure that there won't be any plain text copies of this array anywhere else in the memory
			//reason is that some JVMs may have copied the array without updating all copies until GC collects them
			Arrays.fill(Arrays.toString(plainTextPassword.password).toCharArray(), '\0');

			SecretKeyFactory keyFactory = providerService.getSecretKeyFactory();
			SecretKey key = keyFactory.generateSecret(keySpec);

			//todo-sv: check if returning secretKey or byte array is better
			//what is the difference
			EncryptedPassword encPwd = new EncryptedPassword();
			encPwd.setPassword(key.getEncoded());
			encPwd.setSecretKey(key);
			return encPwd;
		}
		catch (InvalidKeySpecException ex) {
			PieLogger.error(this.getClass(), "Encryption failed!", ex);
		}
		return null;
	}
}
