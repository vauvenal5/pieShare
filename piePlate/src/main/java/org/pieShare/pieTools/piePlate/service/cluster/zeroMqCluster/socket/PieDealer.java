/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket;

import java.net.InetAddress;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.api.IPieDealer;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

/**
 *
 * @author Paul
 */
public class PieDealer implements IPieDealer {
     
    private ZMQ.Socket dealer;
    private ZeroMQUtilsService utils;
    
    public PieDealer()
    {
        ZMQ.Context context = ZMQ.context(1);
        this.dealer = context.socket(ZMQ.DEALER);
        this.utils = new ZeroMQUtilsService();
    }

    @Override
    public boolean connect(InetAddress address) {
        try{
            PieLogger.trace(PieDealer.class, "Connecting to {0}", utils.buildConnectionString(address));
            dealer.connect(utils.buildConnectionString(address));
            return true;
        }catch(ZMQException e){
            PieLogger.error(PieDealer.class, "Connection failed {0}", e);
            return false;
        }
    }

    @Override
    public void disconnect(InetAddress address) {
        PieLogger.trace(PieDealer.class, "Connecting to {0}", utils.buildConnectionString(address));
        dealer.disconnect(utils.buildConnectionString(address));
    }

    @Override
    public void close() {
        dealer.close();
    }

    @Override
    public void send(byte[] message) {
        try{
            dealer.send(message, ZMQ.NOBLOCK);
        }catch(ZMQException e){
            PieLogger.error(PieDealer.class, "Message send error: {0}", e);
        }
    }
 }
