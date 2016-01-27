/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.channel;

import org.pieShare.pieTools.piePlate.model.message.api.IEncryptedMessage;
import org.pieShare.pieTools.piePlate.service.channel.exception.PieChannelException;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.encodeService.api.IEncodeService;

/**
 *
 * @author sveto_000
 */
public class SymmetricEncryptedChannel extends PieChannel<IEncryptedMessage> {

	//todo-sv: this class quite probably really urgently needs an interface
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

			int kk = 0;
			for (int i = 0; i < message.length - 1; i++) {
				kk = (message[i] ^ message[i + 1]);
			}
			PieLogger.error(this.getClass(), "Error Encryption: " + kk);
			throw new PieChannelException(ex);
		}
	}

}
