/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.discovery;

import java.net.InetAddress;
import javax.inject.Provider;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import org.pieShare.pieTools.piePlate.model.DiscoveredMember;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.IMemberDiscoveredListener;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.MemberDiscoveredEvent;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class ZeroconfigDiscoveryListener implements IJmdnsDiscoveryListener {
	
	private IEventBase<IMemberDiscoveredListener, MemberDiscoveredEvent> memberDiscoveredEventBase;
	private Provider<DiscoveredMember> discoveredMemberProvider;

	@Override
	public IEventBase<IMemberDiscoveredListener, MemberDiscoveredEvent> getMemberDiscoveredEventBase() {
		return memberDiscoveredEventBase;
	}

	public void setMemberDiscoveredEventBase(IEventBase<IMemberDiscoveredListener, MemberDiscoveredEvent> memberDiscoveredEventBase) {
		this.memberDiscoveredEventBase = memberDiscoveredEventBase;
	}

	public void setDiscoveredMemberProvider(Provider<DiscoveredMember> discoveredMemberProvider) {
		this.discoveredMemberProvider = discoveredMemberProvider;
	}

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
		PieLogger.trace(this.getClass(), "New Service Resolved-Event with name {} and port {}.", event.getName(), event.getInfo().getPort());
		for(InetAddress ad: event.getInfo().getInetAddresses()) {
			DiscoveredMember member = discoveredMemberProvider.get();
			member.setInetAdresses(ad);
			member.setPort(event.getInfo().getPort());
			//todo: DI think about how we could properly inject prototyped objects which need constructor parameters
			memberDiscoveredEventBase.fireEvent(new MemberDiscoveredEvent(this, member));
		}
	}
	
}
