/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.discovery;

import java.io.IOException;
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

	private ServiceListener listener;

	public ZeroconfigDiscoveryService() {
	}

	public void setDiscoveredMemberProvider(Provider<DiscoveredMember> discoveredMemberProvider) {
		this.discoveredMemberProvider = discoveredMemberProvider;
	}

	public void setNetworkService(INetworkService networkService) {
		this.networkService = networkService;
	}

	public void setListener(ServiceListener listener) {
		this.listener = listener;
	}

	private void initJmdns() throws DiscoveryException {
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
			this.initJmdns();
			this.myself = ServiceInfo.create(this.type, UUID.randomUUID().toString(), clusterName, port, "");
			this.jmDns.registerService(this.myself);
			this.jmDns.addServiceListener(this.type, listener);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Could not create zeroconfig discovery.", ex);
			throw new DiscoveryException("Could not register service!", ex);
		}
	}

	@Override
	public List<DiscoveredMember> list(String clusterName) throws DiscoveryException {
		this.initJmdns();
		Map<String, ServiceInfo[]> map = this.jmDns.listBySubtype(this.type);
		List<DiscoveredMember> members = new ArrayList<DiscoveredMember>();

		if (!map.containsKey(clusterName)) {
			return members;
		}

		ServiceInfo[] infos = map.get(clusterName);

		for (ServiceInfo info : infos) {
			if ((this.myself == null) || !info.getName().equals(this.myself.getName())) {
				//todo-discovery: it could lead to problems if it retunrs multiple inetAdresses
				for (InetAddress ad : info.getInetAddresses()) {
					DiscoveredMember member = discoveredMemberProvider.get();
					member.setInetAdresses(ad);
					member.setPort(info.getPort());
					members.add(member);
				}
			}
		}
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
}
