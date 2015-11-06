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
    
    private ZMQ.Socket router;
    private ZeroMQUtilsService utils;
    
    public PieRouter()
    {
        ZMQ.Context context = ZMQ.context(1);
        this.router = context.socket(ZMQ.ROUTER);
        this.utils = new ZeroMQUtilsService();
    }
    
    @Override
    public boolean bind(InetAddress address) {
        PieLogger.trace(PieRouter.class, "Bind router on: {0}", utils.buildConnectionString(address));
        try{
            router.bind(utils.buildConnectionString(address));
            return true;
        }catch(ZMQException e){
            PieLogger.error(PieRouter.class, "Bind failed: {0}", e);
            return false;
        }
    }

    @Override
    public void close() {
        router.close();
    }

    @Override
    public byte[] receive() {
         return router.recv();
    }
}
