/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.discovery;

import java.net.InetAddress;
import javax.inject.Provider;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
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
	private String myself;
	private String cloudName;
	private ZeroconfigDiscoveryService discoveryService;

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
	public void setDiscoveryService(ZeroconfigDiscoveryService discoveryService) {
		this.discoveryService = discoveryService;
	}

	@Override
	public void setMyself(String myself) {
		this.myself = myself;
	}

	@Override
	public void setCloudName(String cloudName) {
		this.cloudName = cloudName;
	}

	@Override
	public void serviceAdded(ServiceEvent event) {
		PieLogger.trace(this.getClass(), "New Service Added-Event with name {} and port {}.", event.getName(), event.getInfo().getPort());

		ServiceInfo info = event.getInfo();

		if (info.getPort() == 0) {
			PieLogger.trace(this.getClass(), "Discarding 0 port! {}", info.getName());
			return;
		}

		ServiceInfo i = this.discoveryService.resolveService(info);
		this.discovered(i);
		//discovered(this.discoveryService.resolveService(info));
	}

	@Override
	public void serviceRemoved(ServiceEvent event) {
		PieLogger.trace(this.getClass(), "New Service Removed-Event with name {}.", event.getName());
	}

	@Override
	public void serviceResolved(ServiceEvent event) {
		PieLogger.trace(this.getClass(), "New Service Resolved-Event with name {} and port {}.", event.getName(), event.getInfo().getPort());

		discovered(event.getInfo());
	}

	private void discovered(ServiceInfo info) {
		if (myself.equals(info.getName())) {
			PieLogger.trace(this.getClass(), "Discarding myself! {}", info.getName());
			return;
		}

		//todo: this really needs to be added otherwise all pieShare instances 
		//in the network are added to our cluster and not only the one of our user
		//however there is some kind issue with resolving the subtype
		if(!info.getSubtype().equals(this.cloudName)) {
			PieLogger.trace(this.getClass(), "Discarding instance from other cloud! {}", info.getSubtype());
			return;
		}
		
		for (InetAddress ad : info.getInetAddresses()) {
			DiscoveredMember member = this.discoveredMemberProvider.get();
			member.setInetAdresses(ad);
			member.setPort(info.getPort());
			member.setName(info.getName());
			PieLogger.trace(this.getClass(), "Triggering event for {} {}", member.getInetAdresses().getHostAddress(), member.getPort());
			//todo: DI think about how we could properly inject prototyped objects which need constructor parameters
			memberDiscoveredEventBase.fireEvent(new MemberDiscoveredEvent(this, member));
		}
	}

}
