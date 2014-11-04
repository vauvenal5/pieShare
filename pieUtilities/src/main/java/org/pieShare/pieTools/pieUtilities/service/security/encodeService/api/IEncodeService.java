/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.security.encodeService.api;

import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;

/**
 *
 * @author Richard
 */
public interface IEncodeService {

	byte[] encrypt(PlainTextPassword passphrase, byte[] plaintext) throws Exception;

	byte[] decrypt(PlainTextPassword passphrase, byte[] ciphertext) throws Exception;
}
