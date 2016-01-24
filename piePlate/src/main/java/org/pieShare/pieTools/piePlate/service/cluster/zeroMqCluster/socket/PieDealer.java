/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.pieShare.pieTools.piePlate.model.DiscoveredMember;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.IEndpointCallback;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.api.IPieDealer;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.AShutdownableService;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import zmq.ZError;

/**
 *
 * @author Paul
 */
public class PieDealer extends AShutdownableService implements IPieDealer {
	private ZeroMQUtilsService utils;
	private volatile boolean shutdown;

	public PieDealer() {
		this.shutdown = false;
	}

	public void setZeroMQUtilsService(ZeroMQUtilsService service) {
		this.utils = service;
	}
	
	@Override
	public void send(List<DiscoveredMember> members, byte[] message, IEndpointCallback callback) {
		ZMQ.Context ctx = ZMQ.context(1);
        ZMQ.Socket sock = ctx.socket(ZMQ.DEALER);
		
		int endpoints = 0;
		List<DiscoveredMember> brokenMembers = new ArrayList<>();

		//todo-part1: this actually does not work!!! the socket does not throw an
		//exception when he can not connect to the endpoint
		//this is most likely due to zeroMQs reconnection logic
		for (DiscoveredMember member : members) {
			//important: do never change this flag from inside this function!!!
				//this function is used by many threads we would have to change
				//the boolean to an atomic boolean
			if(this.shutdown) {
				sock.close();
				ctx.close();
				return;
			}
			
			try {
				sock.connect(utils.buildConnectionString(member.getInetAdresses(), member.getPort()));
				endpoints++;
			} catch(ZMQException e){
				//http://api.zeromq.org/2-1:zmq-connect
				if((e.getErrorCode() == ZError.EINVAL)
						|| (e.getErrorCode() == ZError.EPROTONOSUPPORT)
						|| (e.getErrorCode() == ZError.ENOCOMPATPROTO)
						|| (e.getErrorCode() == ZError.ETERM)
						|| (e.getErrorCode() == ZError.ENOTSOCK)
						|| (e.getErrorCode() == ZError.EMTHREAD)){
					brokenMembers.add(member);
				}else{
					PieLogger.error(this.getClass(), "Connection error: {}", e);	
				}
			}
		}
						
		PieLogger.trace(this.getClass(), "Sending msg to endpoints.");
		for (int i = 0; (i < endpoints) && !shutdown; i++) {
			try {
				sock.send(message, ZMQ.NOBLOCK);
			} catch (ZMQException e) {
				PieLogger.error(PieDealer.class, "Message send error: {}", e);
			}
		}
		
		sock.close();
		ctx.close();
		
		//todo-part2: due to the fact that we are not able to recognize the
			//the broken endpoints as intendet it is not nesseccary to make
			//this call at all would only make things more difficult.
//		if(brokenMembers.size() > 0 && !shutdown) {
//			callback.nonRespondingEndpoint(brokenMembers);
//		}
	}

	@Override
	public void shutdown() {
		this.shutdown = true;
	}
	
	public void reconnect() {
		this.shutdown = false;
	}
}
