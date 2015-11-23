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
import org.pieShare.pieTools.piePlate.model.IPieAddress;
import org.pieShare.pieTools.piePlate.model.message.api.IClusterMessage;
import org.pieShare.pieTools.piePlate.service.channel.api.IIncomingChannel;
import org.pieShare.pieTools.piePlate.service.channel.api.IOutgoingChannel;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterRemovedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterRemovedListener;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.api.IPieDealer;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class ZeroMqClusterService implements IClusterService {
	
	private IPieDealer dealer;
	private String id;
	
	private List<IIncomingChannel> incomingChannels;
	private Map<String, IOutgoingChannel> outgoingChannels;
	
	public ZeroMqClusterService(){
		this.incomingChannels = new ArrayList<>();
		this.outgoingChannels = new HashMap<>();
	}

	@Override
	public void connect(String clusterName) throws ClusterServiceException {
		//call discovery for endpoints
		//foreach endpoint connect&send ZeroMQClusterMessage
		//	dealer = new PieDealer();
		//
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
		dealer.close();
	}

	@Override
	public void sendMessage(IClusterMessage msg) throws ClusterServiceException {
		IPieAddress address = msg.getAddress();

		if (!this.outgoingChannels.containsKey(address.getChannelId())) {
			throw new ClusterServiceException(String.format("This outgoing channel doesn't exists: %s", address.getChannelId()));
		}

		try {
			PieLogger.debug(this.getClass(), "Sending: {}", msg.getClass());
			byte[] message = this.outgoingChannels.get(msg.getAddress().getChannelId()).prepareMessage(msg);
			this.dealer.send(message);
		}
		catch (Exception e) {
			throw new ClusterServiceException(e);
		}
	}

	//@Override
	public int getMembersCount() {
		//return jmDNS count
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isConnectedToCluster() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public IEventBase<IClusterRemovedListener, ClusterRemovedEvent> getClusterRemovedEventBase() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void registerIncomingChannel(IIncomingChannel channel) {
		incomingChannels.add(channel);
	}

	@Override
	public void registerOutgoingChannel(IOutgoingChannel channel) {
		outgoingChannels.put(channel.getChannelId(), channel);
	}

	@Override
	public List<IIncomingChannel> getIncomingChannels() {
		return incomingChannels;
	}

	@Override
	public boolean isMaster() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
