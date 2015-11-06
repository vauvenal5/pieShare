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
     
    private ZMQ.Context context;
    private ZMQ.Socket dealer;
    private ZeroMQUtilsService utils;
    private int endpoints = 0;
    
    public PieDealer()
    {
        this.context = ZMQ.context(1);
        this.dealer = context.socket(ZMQ.DEALER);
        this.utils = new ZeroMQUtilsService();
    }

    @Override
    public boolean connect(InetAddress address, int port) {
        try{
            PieLogger.trace(PieDealer.class, "Connecting to %s", utils.buildConnectionString(address, port));
            dealer.connect(utils.buildConnectionString(address, port));
            endpoints++;
            return true;
        }catch(ZMQException e){
            PieLogger.error(PieDealer.class, "Connection failed %s", e);
            return false;
        }
    }

    @Override
    public void disconnect(InetAddress address, int port) {
        PieLogger.trace(PieDealer.class, "Connecting to %s", utils.buildConnectionString(address, port));
        dealer.disconnect(utils.buildConnectionString(address, port));
        endpoints--;
    }

    @Override
    public void close() {
        dealer.close();
        context.term();
    }

    @Override
    public void send(byte[] message) {
        try{
            for (int i = 0; i < endpoints; i++) {
                dealer.send(message, ZMQ.NOBLOCK);
            }
        }catch(ZMQException e){
            PieLogger.error(PieDealer.class, "Message send error: {0}", e);
        }
    }
 }
