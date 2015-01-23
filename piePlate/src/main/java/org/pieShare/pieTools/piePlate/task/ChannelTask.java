/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.task;

import org.pieShare.pieTools.piePlate.model.IPieAddress;
import org.pieShare.pieTools.piePlate.model.message.api.IClusterMessage;
import org.pieShare.pieTools.piePlate.service.channel.api.IIncomingChannel;
import org.pieShare.pieTools.piePlate.service.channel.api.ITwoWayChannel;
import org.pieShare.pieTools.piePlate.service.channel.exception.PieChannelException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorTaskFactoryException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author sveto_000
 */
public class ChannelTask implements IPieTask {
	
	private IExecutorService executorService;
	
	private IIncomingChannel channel;
	private byte[] message;
	private IPieAddress address;

	public void setExecutorService(IExecutorService executorService) {
		this.executorService = executorService;
	}

	public void setChannel(IIncomingChannel channel) {
		this.channel = channel;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}

	public void setAddress(IPieAddress address) {
		this.address = address;
	}

	@Override
	public void run() {
		IClusterMessage msg = null;
		try {
			msg = this.channel.handleMessage(message);
			PieLogger.debug(this.getClass(), "Recived: {}", msg.getClass());
			msg.setAddress(address);
			this.executorService.handlePieEvent(msg);
		} catch (PieChannelException ex) {
			PieLogger.info(this.getClass(), "Given channel {} cann't handle message. Stoping work!", channel.getClass());
		} catch (PieExecutorTaskFactoryException ex) {
			PieLogger.error(this.getClass(), String.format("Could not execute task for message: %s", msg), ex);
		}
	}
}
