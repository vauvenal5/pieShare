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
public interface IPieDealer {
    /**
     * Connect dealer to socket on address.
     * @param address
     * @return true if connection was established else false.
     */
    boolean connect(InetAddress address, int port);
    
    /**
     * Close connection to endpoint.
     * @param address 
     */
    void disconnect(InetAddress address, int port);
    
    /**
     * Close socket and terminate all connections.
     */
    void close();
    
    /**
     * Send message to to all sockets this dealer is connected to.
     * If no connection is active message gets dropped.
     * @param message 
     */
    void send(byte[] message);
}
