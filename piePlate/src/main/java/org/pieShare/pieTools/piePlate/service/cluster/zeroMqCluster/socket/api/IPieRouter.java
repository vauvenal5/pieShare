/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.api;

import java.net.InetAddress;
import org.pieShare.pieTools.piePlate.service.channel.api.IIncomingChannel;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieTask;

/**
 *
 * @author Paul
 */
public interface IPieRouter extends IPieTask {
    
    /**
     * Close socket.
     */
    void close();

	/**
	 * Register incoming channel.
	 * @param channel
	 */
	public void registerIncomingChannel(IIncomingChannel channel);
	
	/**
	 * Set the port on which the router task will bind later on.
	 * @param port 
	 */
	public void setPort(int port);
}
