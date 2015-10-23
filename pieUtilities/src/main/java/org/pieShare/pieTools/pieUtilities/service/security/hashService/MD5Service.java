/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.security.hashService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import org.apache.commons.lang3.Validate;
import org.pieShare.pieTools.pieUtilities.service.security.IProviderService;

/**
 *
 * @author richy
 */
public class MD5Service implements IHashService {

	private IProviderService provider;

	public void setProviderService(IProviderService service) {
		this.provider = service;
	}

	public MD5Service() {

	}

	@Override
	public byte[] hash(byte[] data) {
		MessageDigest messageDigest = provider.getMessageDigest();
		Validate.notNull(messageDigest);
		messageDigest.update(data);
		byte[] resultByte = messageDigest.digest();
		messageDigest.reset();

		return resultByte;
	}

	@Override
	public byte[] hashStream(InputStream stream) throws IOException {
		//todo: maybe the stream should be created in here instead outside
		//this way this function can close the stream in the end
		MessageDigest messageDigest = provider.getMessageDigest();

		byte[] buffer = new byte[1024];
		int read = 0;

		while ((read = stream.read(buffer)) != -1) {
			Validate.notNull(messageDigest);
			messageDigest.update(buffer, 0, read);
		}

		byte[] resultByte = messageDigest.digest();
		messageDigest.reset();
		return resultByte;
	}

	@Override
	public byte[] hashStream(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		byte[] res = this.hashStream(fis);
		fis.close();
		return res;
	}

	@Override
	public boolean isMD5Equal(byte[] first, byte[] second) {
		if (first.length != second.length) {
			return false;
		}

		for (int i = 0; i < first.length; i++) {
			if (first[i] != second[i]) {
				return false;
			}
		}
		return true;
	}
}
