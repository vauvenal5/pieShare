/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.security.hashService;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import org.apache.commons.lang3.Validate;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.IProviderService;

/**
 *
 * @author richy
 */
public class MD5Service implements IHashService {

	private static final PieLogger md5Logger = new PieLogger(MD5Service.class);

	private IProviderService provider;

	public void setProviderService(IProviderService service) {
		this.provider = service;
	}

	public MD5Service() {

	}

	@Override
	public byte[] hash(byte[] data) {
		MessageDigest messageDigest = this.getMessageDigest();
		Validate.notNull(messageDigest);
		messageDigest.update(data);
		byte[] resultByte = messageDigest.digest();
		messageDigest.reset();

		return resultByte;
	}
	
	private MessageDigest getMessageDigest() {
		MessageDigest messageDigest = null;
		
		try {
			messageDigest = MessageDigest.getInstance(this.provider.getFileHashAlorithm(), this.provider.getProviderName());
		} catch (NoSuchAlgorithmException ex) {
			md5Logger.error("Error in MD5 Hash Algorithm, this shold no happen. Message: " + ex.getMessage());
		} catch (NoSuchProviderException ex) {
			//todo: error handling
		}
		
		return messageDigest;
	}

	@Override
	public byte[] hashStream(InputStream stream) throws IOException {
		MessageDigest messageDigest = this.getMessageDigest();
		
		byte[] buffer = new byte[1024];
		int read = 0;
		PieLogger.debug(this.getClass(),"testing logger");
		while ((read = stream.read(buffer)) != -1) {
			Validate.notNull(messageDigest);
			messageDigest.update(buffer, 0, read);
		}

		byte[] resultByte = messageDigest.digest();
		messageDigest.reset();
		return resultByte;
	}

	@Override
	public boolean isMD5Equal(byte[] first, byte[] second) {
		if(first.length != second.length) return false;
		
		for(int i = 0; i < first.length; i++){
			if(first[i] != second[i]){
				return false;
			}
		}
		return true;
	}

}
