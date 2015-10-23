/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.security;

import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;

/**
 *
 * @author Svetoslav
 */
public interface IProviderService {

	String getProviderName();

	Cipher getFileHashCipher();

	Cipher getPasswordEncryptioCipher();

	Cipher getEnDeCryptCipher();
	
	SecretKeyFactory getSecretKeyFactory();
	
	MessageDigest getMessageDigest();
}
