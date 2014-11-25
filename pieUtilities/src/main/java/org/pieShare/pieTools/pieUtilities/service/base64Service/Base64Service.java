/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.base64Service;

import org.apache.commons.codec.binary.Base64;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;

/**
 *
 * @author richy
 */
public class Base64Service implements IBase64Service {

	@Override
	public byte[] encode(byte[] in) {
		return Base64.encodeBase64(in);
	}

	@Override
	public byte[] decode(byte[] in) {
		return Base64.decodeBase64(in);
	}

}
