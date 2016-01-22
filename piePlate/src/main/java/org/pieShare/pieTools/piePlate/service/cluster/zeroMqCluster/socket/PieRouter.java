/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket;

import java.net.InetAddress;
import java.nio.channels.ClosedSelectorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Provider;
import org.pieShare.pieTools.piePlate.model.DiscoveredMember;
import org.pieShare.pieTools.piePlate.model.PieAddress;
import org.pieShare.pieTools.piePlate.service.channel.api.IIncomingChannel;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.IEndpointCallback;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.api.IPieRouter;
import org.pieShare.pieTools.piePlate.task.ChannelTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

/**
 *
 * @author Paul
 */
public class PieRouter implements IPieRouter {

	private ZeroMQUtilsService utils;
	private IExecutorService executorService;
	private Provider<ChannelTask> channelTaskProvider;
	private List<IIncomingChannel> incomingChannels;
	private boolean shutdown;
	private int port;

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
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void close() {
		shutdown = true;
		PieDealer dealer = new PieDealer();
		dealer.setZeroMQUtilsService(utils);
		List<DiscoveredMember> members = new ArrayList<>();
		DiscoveredMember member = new DiscoveredMember();
		member.setInetAdresses(InetAddress.getLoopbackAddress());
		member.setPort(port);
		member.setName("mySelf");
		members.add(member);
		dealer.send(members, "!shutdown".getBytes(), new IEndpointCallback() {
			@Override
			public void nonRespondingEndpoint(List<DiscoveredMember> brokenMembers) {
			}
		});
		dealer.shutdown();
	}

	@Override
	public void run() {
		//When not bound to 0.0.0.0 MAC will not see the port as bound but only
		//in combination with the specific address which leads to wrong behaviour.
		//More clear explanation:
		//When you bind to a specific adress for example 192.168.0.15:PORT
		//ServerSocket.bind(PORT) will be a valid operation on MAC
		//and because of this the same port would be returned by our 
		//NetworkService as free again.
		//Further it is better if we listen to all addresse:
		//1) to be more compatible with devices working with multiple Nets
		//2) to have the same behaviour like in linux and windows
		//The old command was:
		//router.bind(utils.buildConnectionString(address, port));
		//see also NetworkService
		String connectionString = utils.buildConnectionString("0.0.0.0", port);

		ZContext context = new ZContext(1);
		ZMQ.Socket router = context.createSocket(ZMQ.ROUTER);

		PieLogger.trace(PieRouter.class, "Bind router on: {}", connectionString);
		try {
			router.bind(connectionString);

			//clean shutdown, ZMQ poller?
			//zeromq can't block and sleep
			//https://github.com/zeromq/jeromq/blob/master/src/main/java/zmq/SocketBase.java#L712
			//https://github.com/thriftzmq/thriftzmq-java/blob/master/thriftzmq/src/main/java/org/thriftzmq/ProxyLoop.java
			byte[] msg = null;
			while (!shutdown) {
				try {
					//receive dealer identity and discard it
					router.recv();
					msg = router.recv();
				} catch (ZMQException | AssertionError | ClosedSelectorException e) {
					//todo: can this really only happen when shutding down or 
					//also in other scenarios?
					PieLogger.warn(this.getClass(), "Exception in PieRouter! Shuting router down!", e);
					return;
				}
				
				//we need to shutdown in this strange way because ZMQ is not thread save
				//we can not shutdown the socket prperly from another thread
				
				//todo: consider using a 2nd router for receiving the shutdown signal
				//this way we would be independet of the incoming msgs
				//however we would need another pattern to implement this
				if (Arrays.equals(msg, "!shutdown".getBytes())) {
					router.unbind(this.utils.buildConnectionString("0.0.0.0", port));
					context.destroySocket(router);
					context.destroy();
					break;
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
		} catch (ZMQException e) {
			PieLogger.error(PieRouter.class, "Bind failed: {}", e);
		}

	}

	@Override
	public void registerIncomingChannel(IIncomingChannel channel) {
		this.incomingChannels.add(channel);
	}
}
