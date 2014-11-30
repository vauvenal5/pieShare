/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.channel;

import org.pieShare.pieTools.piePlate.model.message.api.IPiePlainTextMessage;
import org.pieShare.pieTools.piePlate.service.channel.exception.PieChannelException;

/**
 *
 * @author sveto_000
 */
public class PlainTextChannel extends PieChannel<IPiePlainTextMessage> {

	@Override
	public byte[] prepareMessage(IPiePlainTextMessage message) throws PieChannelException {
		return super.prepareMsg(message);
	}

	@Override
	public IPiePlainTextMessage handleMessage(byte[] message) throws PieChannelException {
		return super.handleMsg(message);
	}
	
}
