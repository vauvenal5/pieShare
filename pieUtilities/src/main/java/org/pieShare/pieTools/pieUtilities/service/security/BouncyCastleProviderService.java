/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class BouncyCastleProviderService implements IProviderService {

	private String provName;
	private final String passwordEncryptionAlgo = "PBEWithSHAAndTwofish-CBC";
	private final String encryptionAlgo = "AES/CBC/PKCS5Padding";
	private final String fileHashAlgo = "MD5";

	public BouncyCastleProviderService() {
		BouncyCastleProvider prov = new BouncyCastleProvider();
		provName = prov.getName();
		Security.addProvider(prov);
	}

	@Override
	public String getProviderName() {
		return this.provName;
	}

	@Override
	public Cipher getFileHashCipher() {
		try {
			return Cipher.getInstance(this.fileHashAlgo, this.provName);//return "PBEWithSHAAndTwofish-CBC";
		}
		catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException ex) {
			PieLogger.error(this.getClass(), "Hash Error", ex);
		}
		//Should never happen
		return null;
	}

	@Override
	public Cipher getPasswordEncryptioCipher() {
		try {
			return Cipher.getInstance(this.passwordEncryptionAlgo, this.provName);//return "PBEWithSHAAndTwofish-CBC";
		}
		catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException ex) {
			PieLogger.error(this.getClass(), "Cipher Error", ex);
		}
		//Should never happen
		return null;
	}

	@Override
	public Cipher getEnDeCryptCipher() {
		try {
			return Cipher.getInstance(this.encryptionAlgo, this.provName);//return "PBEWithSHAAndTwofish-CBC";
		}
		catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException ex) {
			PieLogger.error(this.getClass(), "Cipher Error", ex);
		}
		//Should never happen
		return null;
	}

	@Override
	public SecretKeyFactory getSecretKeyFactory() {
		try {
			return SecretKeyFactory.getInstance(this.passwordEncryptionAlgo, this.provName);
		}
		catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
			PieLogger.error(this.getClass(), "Error generating secret key factory", ex);
		}
		//This should nerver happen.
		return null;
	}

	@Override
	public MessageDigest getMessageDigest() {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance(this.fileHashAlgo, this.provName);
			return messageDigest;
		}
		catch (NoSuchAlgorithmException ex) {
			PieLogger.error(this.getClass(), "Error in MD5 Hash Algorithm, this should not happen.", ex);
		}
		catch (NoSuchProviderException ex) {
			//todo: error handling
			PieLogger.error(this.getClass(), "Error in MD5 Hash Algorithm.", ex);
		}

		//This should never happen
		return null;
	}

}
