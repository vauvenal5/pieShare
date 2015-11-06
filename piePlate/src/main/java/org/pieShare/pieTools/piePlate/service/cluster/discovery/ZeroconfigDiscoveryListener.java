/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.discovery;

import java.net.InetAddress;
import java.util.EventObject;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import org.pieShare.pieTools.piePlate.model.DiscoveredMember;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.IMemberDiscoveredListener;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.MemberDiscoveredEvent;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventListener;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class ZeroconfigDiscoveryListener implements ServiceListener {
	
	IEventBase<IMemberDiscoveredListener, MemberDiscoveredEvent> memberDiscoveredEventBase;

	@Override
	public void serviceAdded(ServiceEvent event) {
		PieLogger.trace(this.getClass(), "New Service Added-Event with name {}.", event.getName());
	}

	@Override
	public void serviceRemoved(ServiceEvent event) {
		PieLogger.trace(this.getClass(), "New Service Removed-Event with name {}.", event.getName());
	}

	@Override
	public void serviceResolved(ServiceEvent event) {
		for(InetAddress ad: event.getInfo().getInetAddresses()) {
			DiscoveredMember member = new DiscoveredMember();
			memberDiscoveredEventBase.fireEvent(new MemberDiscoveredEvent(this, member));
		}
	}
	
}
