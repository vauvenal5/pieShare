/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.api;

import java.util.List;
import org.pieShare.pieTools.piePlate.model.DiscoveredMember;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.IEndpointCallback;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;

/**
 *
 * @author Paul
 */
public interface IPieDealer extends IShutdownableService { 
    /**
     * Send message to to all sockets this dealer is connected to.
     * If no connection is active message gets dropped.
	 * @param endpoints
     * @param message 
	 * @param callback object for unresponsive endpoints
     */
    void send(List<DiscoveredMember> endpoints, byte[] message, IEndpointCallback callback);
	
	/**
	 * Re-enables the dealer after a network reset.
	 */
	void reconnect();
}
