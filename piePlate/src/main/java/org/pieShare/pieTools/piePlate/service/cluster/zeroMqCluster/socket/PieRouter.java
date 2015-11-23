/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket;

import java.net.InetAddress;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.api.IPieRouter;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

/**
 *
 * @author Paul
 */
public class PieRouter implements IPieRouter{
    
    private ZMQ.Context context;
    private ZMQ.Socket router;
    private ZeroMQUtilsService utils;
    
    public PieRouter()
    {
       
        this.utils = new ZeroMQUtilsService();
    }
    
    @Override
    public boolean bind(InetAddress address, int port) {
        if(context == null){
            this.context = ZMQ.context(1);
            this.router = context.socket(ZMQ.ROUTER);
        }
        
        PieLogger.trace(PieRouter.class, "Bind router on: %s", utils.buildConnectionString(address, port));
        try{
            router.bind(utils.buildConnectionString(address, port));
            return true;
        }catch(ZMQException e){
            PieLogger.error(PieRouter.class, "Bind failed: %s", e);
            return false;
        }
    }

    @Override
    public void close() {
        router.close();
        context.close();
    }

    @Override
    public byte[] receive() {
        //receive dealer identity and discard it
        router.recv();
        return router.recv();
    }
}
