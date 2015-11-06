/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.discovery;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.impl.JmDNSImpl;
import javax.jmdns.impl.ServiceInfoImpl;
import org.pieShare.pieTools.pieUtilities.service.networkService.INetworkService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class ZerconfigDiscoveryService {
	
	private JmDNS jmDns;
	private INetworkService networkService;
	private String type = "_pie._tcp.local.";
	
	public ZerconfigDiscoveryService(INetworkService networkService, String name) {
		this.networkService = networkService;
		try {
			jmDns = new JmDNSImpl(this.networkService.getLocalHost(), name);
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Could not create zeroconfig discovery.", ex);
		}
	}
	
	public void setListener(ServiceListener listener) {
		this.jmDns.addServiceListener(this.type, listener);
	}
	
	public void registerService(int port) {
		try {
			this.jmDns.registerService(ServiceInfo.create(this.type, "pie", port, ""));
		} catch (IOException ex) {
			PieLogger.error(this.getClass(), "Could not register zeroconfig service.", ex);
		}
	}
}
