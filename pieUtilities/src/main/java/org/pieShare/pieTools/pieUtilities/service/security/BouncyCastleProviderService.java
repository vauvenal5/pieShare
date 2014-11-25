/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.security;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author Svetoslav
 */
public class BouncyCastleProviderService implements IProviderService {

	private String provName;

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
	public String getFileHashAlorithm() {
		return "MD5";
	}

	@Override
	public String getPasswordEncryptionAlgorithm() {
		return "PBEWithSHAAndTwofish-CBC";
	}

	@Override
	public String getEnDeCryptAlgorithm() {
		return "AES/CBC/PKCS5Padding";
	}

}
