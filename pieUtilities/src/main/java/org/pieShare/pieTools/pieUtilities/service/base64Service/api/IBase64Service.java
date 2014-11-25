/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.base64Service.api;

/**
 *
 * @author richy
 */
public interface IBase64Service {

	public byte[] encode(byte[] in);

	public byte[] decode(byte[] in);
}
