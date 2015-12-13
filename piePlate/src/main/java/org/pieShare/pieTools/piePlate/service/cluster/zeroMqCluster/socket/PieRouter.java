/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Provider;
import org.pieShare.pieTools.piePlate.model.PieAddress;
import org.pieShare.pieTools.piePlate.service.channel.api.IIncomingChannel;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.api.IPieRouter;
import org.pieShare.pieTools.piePlate.task.ChannelTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

/**
 *
 * @author Paul
 */
public class PieRouter implements IPieRouter {

	private ZMQ.Context context;
	private ZMQ.Socket router;
	private ZeroMQUtilsService utils;
	private IExecutorService executorService;
	private Provider<ChannelTask> channelTaskProvider;
	private List<IIncomingChannel> incomingChannels;
	private boolean shutdown;

	public PieRouter() {
		this.incomingChannels = new ArrayList<>();
		shutdown = false;
	}

	public void setExecutorService(IExecutorService service) {
		this.executorService = service;
	}

	public void setZeroMQUtilsService(ZeroMQUtilsService service) {
		this.utils = service;
	}

	public void setChannelTaskProvider(Provider<ChannelTask> provider) {
		this.channelTaskProvider = provider;
	}

	@Override
    public boolean bind(int port) {
		if (context == null) {
			this.context = ZMQ.context(1);
			this.router = context.socket(ZMQ.ROUTER);
		}
		
		//When not bound to 0.0.0.0 MAC will not see the port as bound but only
		//in combination with the specific address which leads to wrong behaviour.
		//More clear explanation:
		//router.bind(utils.buildConnectionString(address, port));
		String connectionString = utils.buildConnectionString("0.0.0.0", port);
        
        PieLogger.trace(PieRouter.class, "Bind router on: %s", connectionString);
		try {
			router.bind(connectionString);
			return true;
		} catch (ZMQException e) {
			PieLogger.error(PieRouter.class, "Bind failed: %s", e);
			return false;
		}
	}

	@Override
	public void close() {
		shutdown = true;

		router.close();
		context.close();
	}

	@Override
	public byte[] receive() {
		//receive dealer identity and discard it
		router.recv();

		return router.recv();
	}

	@Override
	public void run() {
		byte[] msg = null;
		//clean shutdown, ZMQ poller?
		//zeromq can't block and sleep
		//https://github.com/zeromq/jeromq/blob/master/src/main/java/zmq/SocketBase.java#L712
		//https://github.com/thriftzmq/thriftzmq-java/blob/master/thriftzmq/src/main/java/org/thriftzmq/ProxyLoop.java
		while (!shutdown) {
			try {
				msg = this.receive();
			} catch (ZMQException e) {
				return;
			}
			for (IIncomingChannel channel : incomingChannels) {
				ChannelTask task = this.channelTaskProvider.get();
				task.setChannel(channel);
				task.setMessage(msg);
				PieAddress addr = new PieAddress();
				task.setAddress(addr);

				this.executorService.execute(task);
			}
		}

	}

	@Override
	public void registerIncomingChannel(IIncomingChannel channel) {
		this.incomingChannels.add(channel);
	}
}
