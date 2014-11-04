/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.security.encodeService;

import java.lang.reflect.Field;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.IProviderService;
import org.pieShare.pieTools.pieUtilities.service.security.encodeService.api.IEncodeService;
import org.pieShare.pieTools.pieUtilities.service.security.pbe.IPasswordEncryptionService;

/**
 *
 * @author Richard
 */
public class EncodeService implements IEncodeService {

	private static final String salt = "A long, but constant phrase that will be used each time as the salt.";
	private static final int iterations = 2000;
	private static final int keyLength = 256;
	private IProviderService providerService;
	private IPasswordEncryptionService passwordEncryptionService;
	private final SecureRandom random = new SecureRandom();

	public void setPasswordEncryptionService(IPasswordEncryptionService passwordEncryptionService) {
		this.passwordEncryptionService = passwordEncryptionService;
	}

	public void setProviderService(IProviderService providerService) {
		this.providerService = providerService;
	}

	public EncodeService() {
		removeCryptographyRestrictions();
	}

	private SecretKey generateKey(PlainTextPassword passphrase) throws Exception {
		PBEKeySpec keySpec = new PBEKeySpec(Arrays.toString(passphrase.password).toCharArray(), salt.getBytes(), iterations, keyLength);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC", providerService.getProviderName());
		//SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("AES", providerService.getProviderName());
		return keyFactory.generateSecret(keySpec);
	}

	@Override
	public byte[] encrypt(PlainTextPassword passphrase, byte[] plaintext) throws Exception {
		SecretKey key = passwordEncryptionService.encryptPassword(passphrase).getSecretKey();// generateKey(passphrase);

		Cipher cipher = Cipher.getInstance(providerService.getEnDeCryptAlgorithm(), providerService.getProviderName());
		cipher.init(Cipher.ENCRYPT_MODE, key);//, generateIV(cipher));//, random);
		return cipher.doFinal(plaintext);
	}

	@Override
	public byte[] decrypt(PlainTextPassword passphrase, byte[] ciphertext) throws Exception {
		SecretKey key = passwordEncryptionService.encryptPassword(passphrase).getSecretKey();// generateKey(passphrase);

		Cipher cipher = Cipher.getInstance(providerService.getEnDeCryptAlgorithm(), providerService.getProviderName());
		cipher.init(Cipher.DECRYPT_MODE, key);//, generateIV(cipher), random);
		return cipher.doFinal(ciphertext);
	}

	private IvParameterSpec generateIV(Cipher cipher) throws Exception {
		byte[] ivBytes = new byte[cipher.getBlockSize()];
		random.nextBytes(ivBytes);
		return new IvParameterSpec(ivBytes);
	}

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
