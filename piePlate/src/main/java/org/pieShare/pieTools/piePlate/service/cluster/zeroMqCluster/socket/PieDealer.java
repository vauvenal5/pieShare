/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket;

import java.net.InetAddress;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.api.IPieDealer;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

/**
 *
 * @author Paul
 */
public class PieDealer implements IPieDealer {

	private ZContext context; //switch to ZContext?
	private ZMQ.Socket dealer;
	private ZeroMQUtilsService utils;
	private int endpoints=0;
	private boolean shutdown;

	public PieDealer() {
		this.shutdown = false;
	}

	public void setZeroMQUtilsService(ZeroMQUtilsService service) {
		this.utils = service;
	}

	@Override
	public boolean connect(InetAddress address, int port) {
		if (this.context == null) {
			this.context = new ZContext(1);
			this.dealer = context.createSocket(ZMQ.DEALER);
		}

		try {
			PieLogger.debug(PieDealer.class, "Connecting to %s", utils.buildConnectionString(address, port));
			dealer.connect(utils.buildConnectionString(address, port));
			endpoints++;
			return true;
		} catch (ZMQException e) {
			PieLogger.error(PieDealer.class, "Connection failed %s", e);
			return false;
		}
	}

	@Override
	public void disconnect(InetAddress address, int port) {
		PieLogger.trace(PieDealer.class, "Connecting to {}", utils.buildConnectionString(address, port));
		dealer.disconnect(utils.buildConnectionString(address, port));
		endpoints--;
	}

	@Override
	public void close() {
		if (context != null) {
			context.destroy();
		}
	}

	@Override
	public void send(byte[] message) {
		if(this.shutdown) {
			return;
		}
		try {
			PieLogger.trace(this.getClass(), "Sending msg to endpoints.");
			for (int i = 0; i < endpoints; i++) {
				dealer.send(message, ZMQ.NOBLOCK);
			}
		} catch (ZMQException e) {
			PieLogger.error(PieDealer.class, "Message send error: {}", e);
		}
	}
}
