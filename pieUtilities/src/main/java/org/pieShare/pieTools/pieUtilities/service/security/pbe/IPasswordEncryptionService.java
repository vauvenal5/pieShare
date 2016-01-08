/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.security.pbe;

import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;

/**
 *
 * @author Svetoslav
 */
public interface IPasswordEncryptionService {

	EncryptedPassword encryptPassword(PlainTextPassword plainTextPassword);
         EncryptedPassword getEncryptedPasswordFromExistingSecretKey(byte[] encodedKey);
}
