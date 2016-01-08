/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.discovery;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.inject.Provider;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import org.pieShare.pieTools.piePlate.model.DiscoveredMember;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.IMemberDiscoveredListener;
import org.pieShare.pieTools.pieUtilities.service.networkService.INetworkService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.AShutdownableService;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class ZeroconfigDiscoveryService extends AShutdownableService implements IDiscoveryService {

	private JmDNS jmDns;
	private INetworkService networkService;
	private String type = "_pieShare._pie.local.";
	private ServiceInfo myself;
	private Provider<DiscoveredMember> discoveredMemberProvider;
	private String cloudName;

	private IJmdnsDiscoveryListener listener;

	public ZeroconfigDiscoveryService() {
	}

	public void setDiscoveredMemberProvider(Provider<DiscoveredMember> discoveredMemberProvider) {
		this.discoveredMemberProvider = discoveredMemberProvider;
	}

	public void setNetworkService(INetworkService networkService) {
		this.networkService = networkService;
	}

	public void setListener(IJmdnsDiscoveryListener listener) {
		this.listener = listener;
	}

	private synchronized void initJmdns() throws DiscoveryException {
		if (this.jmDns != null) {
			return;
		}

		try {
			this.jmDns = JmDNS.create(this.networkService.getLocalHost());
		} catch (IOException ex) {
			throw new DiscoveryException("Init of jmdns failed!", ex);
		}
	}

	@Override
	public void registerService(String clusterName, int port) throws DiscoveryException {
		try {
			this.cloudName = clusterName;
			this.initJmdns();
			String me = String.format("%s.%s", clusterName, UUID.randomUUID().toString());
			PieLogger.trace(this.getClass(), "Registering myself with id {}", me);
			this.myself = ServiceInfo.create(this.type, me, port, "");
			this.jmDns.registerService(this.myself);
			
			//todo-sv: resolve circular dependecy
			listener.setDiscoveryService(this);
			listener.setMyself(me);
			listener.setCloudName(clusterName);
			this.jmDns.addServiceListener(this.type, listener);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Could not create zeroconfig discovery.", ex);
			throw new DiscoveryException("Could not register service!", ex);
		}
	}
	
	public ServiceInfo resolveService(ServiceInfo info) {
		return this.jmDns.getServiceInfo(info.getType(), info.getName());
	}

	@Override
	public List<DiscoveredMember> list(String clusterName) throws DiscoveryException {
		this.initJmdns();
		//Map<String, ServiceInfo[]> map = this.jmDns.listBySubtype(this.type);
		ServiceInfo[] list = this.jmDns.list(this.type);
		List<DiscoveredMember> members = new ArrayList<DiscoveredMember>();

		/*PieLogger.trace(this.getClass(), "Found the following amount of items {}.", map.size());

		if (!map.containsKey(clusterName)) {
			int arrayLength = map.values().size();
			PieLogger.trace(this.getClass(), "Found item size {}.", arrayLength);
			for(ServiceInfo[] infos: map.values()) {
				PieLogger.trace(this.getClass(), "Amount of items {}.", infos.length);
				for(ServiceInfo info: infos) {
					PieLogger.trace(this.getClass(), "Item: {}.", info.getSubtype());
				}
			}
			PieLogger.warn(this.getClass(), "No members found for {}", clusterName);
			return members;
		}*/

		//ServiceInfo[] infos = map.get(clusterName);

		//for (ServiceInfo info : infos) {
		for (ServiceInfo info : list) {
			if ((this.myself == null) 
					|| (!info.getName().equals(this.myself.getName())
					&& info.getName().startsWith(this.cloudName))) {
				//todo-discovery: it could lead to problems if it retunrs multiple inetAdresses
				for (InetAddress ad : info.getInetAddresses()) {
					DiscoveredMember member = discoveredMemberProvider.get();
					member.setInetAdresses(ad);
					member.setPort(info.getPort());
					member.setName(info.getName());
					members.add(member);
				}
			}
		}

		PieLogger.trace(this.getClass(), "We discovered {} members!", members.size());

		return members;
	}

	@Override
	public void shutdown() {
		if (jmDns != null) {
			this.jmDns.unregisterAllServices();
			try {
				this.jmDns.close();
			} catch (IOException ex) {
				PieLogger.error(this.getClass(), "Could not close jmdns.", ex);
				//fail silently due to shutdown
			}
		}
	}

	@Override
	public void addMemberDiscoveredListener(IMemberDiscoveredListener listener) {
		this.listener.getMemberDiscoveredEventBase().addEventListener(listener);
	}

	@Override
	public void removeMemberDiscoveredListener(IMemberDiscoveredListener listener) {
		this.listener.getMemberDiscoveredEventBase().removeEventListener(listener);
	}
}
