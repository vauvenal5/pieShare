/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.channel;

import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.service.channel.api.ITwoWayChannel;
import org.pieShare.pieTools.piePlate.service.channel.exception.PieChannelException;
import org.pieShare.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieShare.pieTools.piePlate.service.serializer.exception.SerializerServiceException;

/**
 *
 * @author sveto_000
 */
public abstract class PieChannel<M extends IPieMessage> implements ITwoWayChannel<M> {
	
	private ISerializerService serializerService;
	private String channelId;

	@Override
	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public void setSerializerService(ISerializerService serializerService) {
		this.serializerService = serializerService;
	}

	protected byte[] prepareMsg(M message)  throws PieChannelException {
		try {
			return this.serializerService.serialize(message);
		} catch (SerializerServiceException ex) {
			throw new PieChannelException(ex);
		}
	}

	protected M handleMsg(byte[] message)  throws PieChannelException {
		try {
			return (M)this.serializerService.deserialize(message);
		} catch (SerializerServiceException ex) {
			throw new PieChannelException(ex);
		} catch (ClassCastException ex) {
			throw new PieChannelException("Message type is not allowed on this channel!", ex);
		}
	}
	
}
