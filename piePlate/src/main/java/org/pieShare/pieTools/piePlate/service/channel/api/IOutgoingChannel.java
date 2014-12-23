/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.channel.api;

import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.service.channel.exception.PieChannelException;

/**
 *
 * @author sveto_000
 */
public interface IOutgoingChannel<M extends IPieMessage> {
	String getChannelId();
	byte[] prepareMessage(M message) throws PieChannelException;
}
