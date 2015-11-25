/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Provider;
import org.pieShare.pieTools.piePlate.model.PieAddress;
import org.pieShare.pieTools.piePlate.service.channel.api.IIncomingChannel;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.api.IPieRouter;
import org.pieShare.pieTools.piePlate.task.ChannelTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieTask;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

/**
 *
 * @author Paul
 */
public class PieRouter implements IPieRouter, IPieTask {
    
    private ZMQ.Context context;
    private ZMQ.Socket router;
    private ZeroMQUtilsService utils;
	private IExecutorService executorService;
	private Provider<ChannelTask> channelTaskProvider;
	private List<IIncomingChannel> incomingChannels;
    
    public PieRouter()
    {
		this.incomingChannels = new ArrayList<>();
    }
    
    @Override
    public boolean bind(InetAddress netAddr, int port) {
        if(context == null){
            this.context = ZMQ.context(1);
            this.router = context.socket(ZMQ.ROUTER);
        }
        
        PieLogger.trace(PieRouter.class, "Bind router on: %s", utils.buildConnectionString(netAddr, port));
        try{
            router.bind(utils.buildConnectionString(netAddr, port));
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

	@Override
	public void run() {
		//clean shutdown, ZMQ poller?
		//zeromq can't block and sleep
		//https://github.com/zeromq/jeromq/blob/master/src/main/java/zmq/SocketBase.java#L712
		//https://github.com/thriftzmq/thriftzmq-java/blob/master/thriftzmq/src/main/java/org/thriftzmq/ProxyLoop.java
		while(true){
			byte[] msg = this.receive();
			for(IIncomingChannel channel: incomingChannels) {
				ChannelTask task = this.channelTaskProvider.get();
				task.setChannel(channel);
				task.setMessage(msg);
				PieAddress addr = new PieAddress();
				task.setAddress(addr);

				this.executorService.execute(task);
			}
		}

	}

	@Override
	public void registerIncomingChannel(IIncomingChannel channel) {
		this.incomingChannels.add(channel);
	}
}
