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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.service.security.IProviderService;

/**
 *
 * @author Svetoslav
 */
public class PasswordEncryptionService implements IPasswordEncryptionService {
    
    public byte[] salt;
    public int iterations;
    
    private IProviderService providerService;
    
    public PasswordEncryptionService() {
        
    }
    
    @Override
    public EncryptedPassword encryptPassword(PlainTextPassword plainTextPassword) {
        try {
            PBEKeySpec keySpec = new PBEKeySpec(plainTextPassword.password, salt, iterations);
            //this does not ensure that there won't be any plain text copies of this array anywhere else in the memory
            //reason is that some JVMs may have copied the array without updating all copies until GC collects them
            Arrays.fill(plainTextPassword.password, '\0');
            
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(this.providerService.getPasswordEncryptionAlgorithm(), this.providerService.getProviderName());
            SecretKey key = keyFactory.generateSecret(keySpec);
            
            EncryptedPassword encPwd = new EncryptedPassword();
            encPwd.setPassword(key.getEncoded());
            return encPwd;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(PasswordEncryptionService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(PasswordEncryptionService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(PasswordEncryptionService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
