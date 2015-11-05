/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.api;

import java.net.InetAddress;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;

/**
 *
 * @author Paul
 */
public interface IPieRouter {
    /**
     * Bind socket to address.
     * @param address 
     * @return true if router was bound else false.
     */
    boolean bind(InetAddress address);
    
    /**
     * Close socket.
     */
    void close();
    
    /**
     * Receive messages from connected sockets.
     * @return IPieMessage
     */
    IPieMessage receive();
}
