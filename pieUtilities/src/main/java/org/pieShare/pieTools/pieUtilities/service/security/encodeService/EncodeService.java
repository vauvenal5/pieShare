/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.security.encodeService;

import java.lang.reflect.Field;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
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

    private IProviderService providerService;
    private IPasswordEncryptionService passwordEncryptionService;
    private final String ivValue = "ThisIsAnTestIvVa"; //ToDo: Change this fix IV to a better value (This have to be 16 bytes long)

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
    @Override
    public byte[] encrypt(EncryptedPassword passphrase, byte[] plaintext) throws Exception {
        SecretKey key = passphrase.getSecretKey();// generateKey(passphrase);

        Cipher cipher = providerService.getEnDeCryptCipher();

        if (passphrase.getIv() != null && passphrase.isUseIv()) {
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(passphrase.getIv()));
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        }

        if (cipher.getIV() != null) {
            passphrase.setIv(cipher.getIV());
        }

        return cipher.doFinal(plaintext);
    }

    @Override
    public byte[] decrypt(EncryptedPassword passphrase, byte[] ciphertext) throws Exception {
        SecretKey key = passphrase.getSecretKey();// generateKey(passphrase);

        Cipher cipher = providerService.getEnDeCryptCipher();

        if (passphrase.getIv() != null && passphrase.isUseIv()) {
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(passphrase.getIv()));
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }

        if (cipher.getIV() != null) {
            passphrase.setIv(cipher.getIV());
        }
        //cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(ciphertext);
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
