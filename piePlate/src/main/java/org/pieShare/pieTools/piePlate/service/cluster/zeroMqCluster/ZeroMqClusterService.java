/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import org.pieShare.pieTools.piePlate.model.DiscoveredMember;
import org.pieShare.pieTools.piePlate.model.IPieAddress;
import org.pieShare.pieTools.piePlate.model.message.api.IClusterMessage;
import org.pieShare.pieTools.piePlate.service.channel.api.IIncomingChannel;
import org.pieShare.pieTools.piePlate.service.channel.api.IOutgoingChannel;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.DiscoveryException;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.IDiscoveryService;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.IMemberDiscoveredListener;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.MemberDiscoveredEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterRemovedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterRemovedListener;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.api.IPieDealer;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.api.IPieRouter;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
import org.pieShare.pieTools.pieUtilities.service.networkService.INetworkService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.AShutdownableService;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class ZeroMqClusterService extends AShutdownableService implements IClusterService,
		IMemberDiscoveredListener, IEndpointCallback {

	private IPieDealer dealer;
	private IPieRouter router;
	private int routerPort;
	private String clustername;
	private IDiscoveryService discovery;
	private INetworkService networkService;
	private IExecutorService executor;
	private Map<String, IOutgoingChannel> outgoingChannels;
	private IEventBase<IClusterRemovedListener, ClusterRemovedEvent> clusterRemovedEventBase;
	private List<DiscoveredMember> members;
	private List<DiscoveredMember> brokenEndpoints;
	private Semaphore sendLimit;
	private AtomicBoolean removeEndpoints;
	private AtomicBoolean connected;

	private final int maxDealers = 100;

	public ZeroMqClusterService() {
		this.outgoingChannels = new HashMap<>();
		this.members = new ArrayList<>();
		this.brokenEndpoints = new ArrayList<>();

		sendLimit = new Semaphore(maxDealers, true);

		removeEndpoints = new AtomicBoolean(false);
		connected = new AtomicBoolean(false);
	}

	public void setClusterRemovedEventBase(IEventBase<IClusterRemovedListener, ClusterRemovedEvent> clusterRemovedEventBase) {
		this.clusterRemovedEventBase = clusterRemovedEventBase;
	}

	public void setDiscoveryService(IDiscoveryService discovery) {
		this.discovery = discovery;
	}

	public void setNetworkService(INetworkService networkService) {
		this.networkService = networkService;
	}

	public void setExecutorService(IExecutorService executor) {
		this.executor = executor;
	}

	public void setPieRouter(IPieRouter router) {
		this.router = router;
	}

	public void setPieDealer(IPieDealer dealer) {
		this.dealer = dealer;
	}

	private void discover(String clusterName, int port) throws ClusterServiceException {
		try {
			this.discovery.addMemberDiscoveredListener(this);
			this.discovery.registerService(clusterName, port);
			members = this.discovery.list();

			for (DiscoveredMember m : members) {
				this.connectMemberToCluster(m);
			}
		} catch (DiscoveryException ex) {
			throw new ClusterServiceException(ex);
		}
	}

	@Override
	public void connect(String clusterName) throws ClusterServiceException {
		PieLogger.debug(this.getClass(), "Connecting to cluster {}!", clusterName);
		this.clustername = clusterName;
		this.routerPort = this.networkService.getAvailablePort();

		router.bind(routerPort);

		//start router task
		this.executor.execute(router);

		discover(clusterName, routerPort);

		connected.set(true);
	}

	@Override
	public String getClustername() {
		return clustername;
	}

	@Override
	public void setClustername(String clusterName) {
		this.clustername = clusterName;
	}

	@Override
	public void reconnect() throws ClusterServiceException {
		disconnect();
		discover(clustername, routerPort);
		connected.set(true);
	}

	@Override
	public void disconnect() throws ClusterServiceException {
		connected.set(false);
		try {
			sendLimit.acquire(maxDealers);
			members.clear();
			sendLimit.release(maxDealers);
		} catch (InterruptedException e) {
			PieLogger.warn(this.getClass(), "Disconnect interrupted {}", e);
		}
	}

	@Override
	public void sendMessage(IClusterMessage msg) throws ClusterServiceException {
		if (connected.get()) {
			IPieAddress address = msg.getAddress();

			if (!this.outgoingChannels.containsKey(address.getChannelId())) {
				throw new ClusterServiceException(String.format("This outgoing channel doesn't exists: {}", address.getChannelId()));
			}

			try {
				PieLogger.debug(this.getClass(), "Sending: {}", msg.getClass());
				byte[] message = this.outgoingChannels.get(msg.getAddress().getChannelId()).prepareMessage(msg);

				sendLimit.acquire();
				this.dealer.send(members, message, this);
				sendLimit.release();

			} catch (Exception e) {
				throw new ClusterServiceException(e);
			}
		}
	}

	@Override
	public boolean isConnectedToCluster() {
		return connected.get();
	}

	@Override
	public IEventBase<IClusterRemovedListener, ClusterRemovedEvent> getClusterRemovedEventBase() {
		return this.clusterRemovedEventBase;
	}

	@Override
	public void registerIncomingChannel(IIncomingChannel channel) {
		router.registerIncomingChannel(channel);
	}

	@Override
	public void registerOutgoingChannel(IOutgoingChannel channel) {
		outgoingChannels.put(channel.getChannelId(), channel);
	}

	@Override
	public List<IIncomingChannel> getIncomingChannels() {
		throw new UnsupportedOperationException("Deprecated, won't be supported");
	}

	@Override
	public boolean isMaster() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void handleObject(MemberDiscoveredEvent event) {
		this.connectMemberToCluster(event.getMember());
	}

	private void connectMemberToCluster(DiscoveredMember member) {
		if (this.members.contains(member)) {
			PieLogger.trace(this.getClass(), "Member {} allready registered!", member.getName());
			return;
		}

		PieLogger.trace(this.getClass(), "Member {} registered", member);
		members.add(member);
	}

	@Override
	public void shutdown() {
		router.close();
	}

	@Override
	public void nonRespondingEndpoint(List<DiscoveredMember> members) {
		synchronized (brokenEndpoints) {
			for (DiscoveredMember member : members) {
				if (!brokenEndpoints.contains(member)) {
					brokenEndpoints.add(member);
				}
			}
		}

		if (removeEndpoints.compareAndSet(false, true)) {
			try {
				sendLimit.acquire(maxDealers - 1);
			} catch (InterruptedException e) {
				PieLogger.warn(this.getClass(), "Non responding endpoint semaphore acquisation failed {}", e);
				return;
			}

			removeBrokenEndpoints();

			removeEndpoints.set(false);
			sendLimit.release(maxDealers - 1);
		}
	}

	private void removeBrokenEndpoints() {
		synchronized (brokenEndpoints) {
			synchronized (members) {
				members.removeAll(brokenEndpoints);
			}
			brokenEndpoints.clear();
		}
	}

	@Override
	public void handleRemoveMember(MemberDiscoveredEvent event) {
		synchronized (this.members) {
			if (this.members.contains(event.getMember())) {
				this.members.remove(event.getMember());
			}
		}
	}
}
