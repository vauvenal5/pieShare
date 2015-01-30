/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.protocols;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import org.jgroups.Address;
import org.jgroups.Event;
import org.jgroups.Message;
import org.jgroups.PhysicalAddress;
import org.jgroups.protocols.Discovery;
import static org.jgroups.protocols.Discovery.marshal;
import org.jgroups.protocols.PingData;
import org.jgroups.protocols.PingHeader;
import org.jgroups.stack.IpAddress;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.util.Responses;
import org.jgroups.util.UUID;
import org.pieShare.pieTools.piePlate.service.loophole.api.ILoopHoleFactory;

/**
 *
 * @author Svetoslav
 */
public class LoopHoleDiscovery  extends Discovery {
	
	private ILoopHoleFactory loopHoleFactory;

	public void setLoopHoleFactory(ILoopHoleFactory loopHoleFactory) {
		this.loopHoleFactory = loopHoleFactory;
	}
	
	public LoopHoleDiscovery() {
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	protected void findMembers(List<Address> members, boolean initial_discovery, Responses responses) {
		PhysicalAddress physical_addr=(PhysicalAddress)down(new Event(Event.GET_PHYSICAL_ADDRESS, local_addr));

        // https://issues.jboss.org/browse/JGRP-1670
        PingData data=new PingData(local_addr, false, UUID.get(local_addr), physical_addr);
        PingHeader hdr=new PingHeader(PingHeader.GET_MBRS_REQ).clusterName(cluster_name);

        if(members != null && members.size() <= max_members_in_discovery_request)
            data.mbrs(members);
		
		for(InetSocketAddress ad: this.loopHoleFactory.getMembers()) {
			// message needs to have DONT_BUNDLE flag: if A sends message M to B, and we need to fetch B's physical
			// address, then the bundler thread blocks until the discovery request has returned. However, we cannot send
			// the discovery *request* until the bundler thread has returned from sending M
			Message msg=new Message(new IpAddress(ad)).putHeader(getId(),hdr).setBuffer(marshal(data))
			  .setFlag(Message.Flag.INTERNAL,Message.Flag.DONT_BUNDLE,Message.Flag.OOB)
			  .setTransientFlag(Message.TransientFlag.DONT_LOOPBACK);
			down_prot.down(new Event(Event.MSG, msg));
		}
	}
	
}
