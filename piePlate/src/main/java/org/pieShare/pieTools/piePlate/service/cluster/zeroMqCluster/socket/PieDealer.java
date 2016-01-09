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
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import zmq.ZError;

/**
 *
 * @author Paul
 */
public class PieDealer implements IPieDealer {
	private ZeroMQUtilsService utils;

	public PieDealer() {
	}

	public void setZeroMQUtilsService(ZeroMQUtilsService service) {
		this.utils = service;
	}
	
	@Override
	public void send(List<DiscoveredMember> members, byte[] message, IEndpointCallback callback) {
		ZContext ctx = new ZContext(1);
		ZMQ.Socket sock = ctx.createSocket(ZMQ.DEALER);
		int endpoints = 0;
		List<DiscoveredMember> brokenMembers = new ArrayList<>();

		for (DiscoveredMember member : members) {
			try{
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
		for (int i = 0; i < endpoints; i++) {
			try {
				sock.send(message, ZMQ.NOBLOCK);
			} catch (ZMQException e) {
				PieLogger.error(PieDealer.class, "Message send error: {}", e);
			}
		}
		
		ctx.destroy();
		callback.NonRespondingEndpoint(brokenMembers);
	}
}
