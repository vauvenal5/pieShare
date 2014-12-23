/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.channel;

import org.pieShare.pieTools.piePlate.model.message.api.IEncryptedMessage;
import org.pieShare.pieTools.piePlate.service.channel.exception.PieChannelException;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.service.security.encodeService.api.IEncodeService;

/**
 *
 * @author sveto_000
 */
public class SymmetricEncryptedChannel extends PieChannel<IEncryptedMessage> {
	
	private IEncodeService encoderService;
	//todo: change this to something like SymetricKey
	private EncryptedPassword encPwd;

	public void setEncoderService(IEncodeService encoderService) {
		this.encoderService = encoderService;
	}

	public void setEncPwd(EncryptedPassword encPwd) {
		this.encPwd = encPwd;
	}

	@Override
	public byte[] prepareMessage(IEncryptedMessage message) throws PieChannelException {
		try {
			byte[] msg = super.prepareMsg(message);
			return this.encoderService.encrypt(encPwd, msg);
		} catch (Exception ex) {
			throw new PieChannelException(ex);
		}
	}

	@Override
	public IEncryptedMessage handleMessage(byte[] message) throws PieChannelException {
		try {
			byte[] msg = this.encoderService.decrypt(encPwd, message);
			return super.handleMsg(msg);
		} catch (Exception ex) {
			throw new PieChannelException(ex);
		}
	}
	
}
