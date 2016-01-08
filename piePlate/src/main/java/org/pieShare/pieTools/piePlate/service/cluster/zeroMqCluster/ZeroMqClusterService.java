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
public class ZeroMqClusterService extends AShutdownableService implements IClusterService, IMemberDiscoveredListener, IEndpointCallback {

	private IPieDealer dealer;
	private IPieRouter router;
	private String id;
	private IDiscoveryService discovery;
	private INetworkService networkService;
	private IExecutorService executor;
	private Map<String, IOutgoingChannel> outgoingChannels;
	private IEventBase<IClusterRemovedListener, ClusterRemovedEvent> clusterRemovedEventBase;
	private List<DiscoveredMember> members;
	private List<DiscoveredMember> brokenEndpoints;
	private Semaphore lowPriority;
	private Semaphore highPriority;
	private Semaphore sendLimit;
	private boolean removeEndpoints;
	
	private final int maxDealers = 100;

	public ZeroMqClusterService() {
		this.outgoingChannels = new HashMap<>();
		this.members = new ArrayList<>();
		this.brokenEndpoints = new ArrayList<>();
		
		lowPriority = new Semaphore(maxDealers);
		highPriority = new Semaphore(1);
		sendLimit = new Semaphore(maxDealers);
		
		removeEndpoints = false;
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

	@Override
	public void connect(String clusterName) throws ClusterServiceException {
		try {
			PieLogger.debug(this.getClass(), "Connecting to cluster {}!", clusterName);
			int routerPort = this.networkService.getAvailablePort();
			
			router.bind(routerPort);

			//start router task
			this.executor.execute(router);

			this.discovery.addMemberDiscoveredListener(this);
			this.discovery.registerService(clusterName, routerPort);
			members = this.discovery.list(clusterName);
			
			for (DiscoveredMember m : members) {
				this.connectMemberToCluster(m);
			}
		} catch (DiscoveryException ex) {
			throw new ClusterServiceException(ex);
		}
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void disconnect() throws ClusterServiceException {
		
	}

	@Override
	public void sendMessage(IClusterMessage msg) throws ClusterServiceException {
		IPieAddress address = msg.getAddress();

		if (!this.outgoingChannels.containsKey(address.getChannelId())) {
			throw new ClusterServiceException(String.format("This outgoing channel doesn't exists: {}", address.getChannelId()));
		}

		try {
			PieLogger.debug(this.getClass(), "Sending: {}", msg.getClass());
			byte[] message = this.outgoingChannels.get(msg.getAddress().getChannelId()).prepareMessage(msg);
			
			lowPriority.acquire();
			highPriority.acquire();
			sendLimit.acquire();
			highPriority.release();
			this.dealer.send(members, message, this);
			sendLimit.release();
			lowPriority.release();
			
			//Sem2 rel
		} catch (Exception e) {
			throw new ClusterServiceException(e);
		}
	}

	@Override
	public boolean isConnectedToCluster() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
	public void NonRespondingEndpoint(DiscoveredMember member) {
		if(!brokenEndpoints.contains(member)){
			brokenEndpoints.add(member);
						
			if(!removeEndpoints){
				removeEndpoints = true;
				try{
					highPriority.acquire();
					sendLimit.acquire(maxDealers);
				}catch(Exception e) {
				}
				//finally
				
				highPriority.release();
				
				RemoveBrokenEndpoints();
				
				sendLimit.release(maxDealers);
			}
		}
	}
	
	private void RemoveBrokenEndpoints(){
		members.removeAll(brokenEndpoints);
		brokenEndpoints.clear();
	}
}
