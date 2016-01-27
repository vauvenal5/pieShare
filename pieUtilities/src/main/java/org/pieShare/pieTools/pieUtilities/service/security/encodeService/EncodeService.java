/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.security.encodeService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.IProviderService;
import org.pieShare.pieTools.pieUtilities.service.security.encodeService.api.IEncodeService;
import org.pieShare.pieTools.pieUtilities.service.security.pbe.IPasswordEncryptionService;

/**
 *
 * @author Richard
 */
public class EncodeService implements IEncodeService {

	private IProviderService providerService;
	private IPasswordEncryptionService passwordEncryptionService;
	private final String ivValue = "ThisIsAnTestIvVa"; //ToDo: Change this fix IV to a better value (This have to be 16 bytes long)
	private IBase64Service base64Service;

	public void setBase64Service(IBase64Service base64Service) {
		this.base64Service = base64Service;
	}

	public void setPasswordEncryptionService(IPasswordEncryptionService passwordEncryptionService) {
		this.passwordEncryptionService = passwordEncryptionService;
	}

	public void setProviderService(IProviderService providerService) {
		this.providerService = providerService;
	}

	public EncodeService() {
		removeCryptographyRestrictions();
	}

	//todo: rewrite encoderService not to throw general Exception
	//todo: encoderService has to be renamed: it is a symmetric encoding
	//we will also need asymmetric
	/*@Override
	public byte[] encrypt(EncryptedPassword passphrase, byte[] plaintext) throws Exception {
		SecretKey key = passphrase.getSecretKey();// generateKey(passphrase);

		Cipher cipher = providerService.getEnDeCryptCipher();

		if (passphrase.getIv() != null && passphrase.isUseIv()) {
			PieLogger.debug(this.getClass(), "Init crypto with IV in encrypt mode!");
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(passphrase.getIv()));
		} else {
			PieLogger.debug(this.getClass(), "Init crypto without IV in encrypt mode!");
			cipher.init(Cipher.ENCRYPT_MODE, key);
		}

		if (cipher.getIV() != null) {
			passphrase.setIv(cipher.getIV());
		}

		return base64Service.encode(cipher.doFinal(plaintext));
	}*/
	@Override
	public byte[] encrypt(EncryptedPassword passphrase, byte[] plaintext) throws Exception {

		SecretKey key = passphrase.getSecretKey();
		//ByteArrayInputStream byteInputStream = new ByteArrayInputStream(base64Service.encode(plaintext));

		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(plaintext);
		
		
		Cipher encryptCipher = providerService.getEnDeCryptCipher();
		encryptCipher.init(Cipher.ENCRYPT_MODE, key);
		int bb = encryptCipher.getBlockSize();

		CipherInputStream cipherStream = new CipherInputStream(byteInputStream, encryptCipher);

		Base64InputStream base64InputStream = new Base64InputStream(cipherStream, true);
		
		byte[] buffer = new byte[bb];
		int noBytes = 0;
		
		while ((noBytes = base64InputStream.read(buffer)) != -1) {

			byteOutputStream.write(buffer, 0, noBytes);
		}
		cipherStream.close();
		byteOutputStream.close();
		byteInputStream.close();

		//byte[] result = byteOutputStream.toByteArray();
		//byte[] base64 = base64Service.encode(result);
		//return base64;
		return byteOutputStream.toByteArray();
	}

	@Override
	public byte[] decrypt(EncryptedPassword passphrase, byte[] ciphertext) throws Exception {
		//optionally put the IV at the beggining of the cipher file
		//fos.write(IV, 0, IV.length);

		SecretKey key = passphrase.getSecretKey();
		//ByteArrayInputStream byteInputStream = new ByteArrayInputStream(base64Service.decode(ciphertext));
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(ciphertext);
		
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		
		Cipher encryptCipher = providerService.getEnDeCryptCipher();
		encryptCipher.init(Cipher.DECRYPT_MODE, key);
		int bb = encryptCipher.getBlockSize();

		//Base64InputStream base64Input = new Base64InputStream(byteInputStream);
		CipherOutputStream cipherStream = new CipherOutputStream(byteOutputStream, encryptCipher);
		Base64OutputStream base64OutputStream = new Base64OutputStream(cipherStream, false);
		
		byte[] buffer = new byte[bb];
		int noBytes = 0;

		while ((noBytes = byteInputStream.read(buffer)) != -1) {
			base64OutputStream.write(buffer, 0, noBytes);
		}

		cipherStream.close();
		byteOutputStream.close();
		byteInputStream.close();

		//return base64Service.decode(byteOutputStream.toByteArray());
		return byteOutputStream.toByteArray();
	}

	//ToDo: Check restriction things. These 2 methods are a HACK
	private static void removeCryptographyRestrictions() {
		if (!isRestrictedCryptography()) {
			PieLogger.debug(EncodeService.class, "Cryptography restrictions removal not needed");
			return;
		}
		try {
			/*
			 * Do the following, but with reflection to bypass access checks:
			 *
			 * JceSecurity.isRestricted = false;
			 * JceSecurity.defaultPolicy.perms.clear();
			 * JceSecurity.defaultPolicy.add(CryptoAllPermission.INSTANCE);
			 */
			final Class<?> jceSecurity = Class.forName("javax.crypto.JceSecurity");
			final Class<?> cryptoPermissions = Class.forName("javax.crypto.CryptoPermissions");
			final Class<?> cryptoAllPermission = Class.forName("javax.crypto.CryptoAllPermission");

			final Field isRestrictedField = jceSecurity.getDeclaredField("isRestricted");
			isRestrictedField.setAccessible(true);
			isRestrictedField.set(null, false);

			final Field defaultPolicyField = jceSecurity.getDeclaredField("defaultPolicy");
			defaultPolicyField.setAccessible(true);
			final PermissionCollection defaultPolicy = (PermissionCollection) defaultPolicyField.get(null);

			final Field perms = cryptoPermissions.getDeclaredField("perms");
			perms.setAccessible(true);
			((Map<?, ?>) perms.get(defaultPolicy)).clear();

			final Field instance = cryptoAllPermission.getDeclaredField("INSTANCE");
			instance.setAccessible(true);
			defaultPolicy.add((Permission) instance.get(null));

			PieLogger.debug(EncodeService.class, "Successfully removed cryptography restrictions");
		} catch (final Exception e) {
			PieLogger.debug(EncodeService.class, "Failed to remove cryptography restrictions", e);
		}
	}

	private static boolean isRestrictedCryptography() {
		// This simply matches the Oracle JRE, but not OpenJDK.
		return "Java(TM) SE Runtime Environment".equals(System.getProperty("java.runtime.name"));
	}
}
