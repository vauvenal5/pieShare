/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.pieshareserver.services.loopHoleService;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieTools.piePlate.model.message.api.IBasePieMessage;
import org.pieShare.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieShare.pieTools.piePlate.service.serializer.exception.SerializerServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieshare.pieshareserver.services.loopHoleService.api.ILoopHoleService;
import org.pieshare.pieshareserver.services.loopHoleService.api.IUserPersistanceService;
import org.pieshare.pieshareserver.tasks.LoopHoleListenerTask;

/**
 *
 * @author Richard
 */
public class LoopHoleService implements ILoopHoleService {

	private final int serverPort;
	private IBeanService beanService;
	private DatagramSocket socket;
	private PieExecutorService executorService;
	private IUserPersistanceService userPersistanceService;
	private ISerializerService serializerService;

	public void setSerializerService(ISerializerService serializerService) {
		this.serializerService = serializerService;
	}
	
	public void setUserPersistanceService(IUserPersistanceService userPersistanceService) {
		this.userPersistanceService = userPersistanceService;
	}
	
	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setExecutorService(PieExecutorService executorService) {
		this.executorService = executorService;
	}

	public LoopHoleService() {
		PieLogger.info(this.getClass(), "Set up Loop Hole Service!");
		serverPort = 6312;
		try {
			socket = new DatagramSocket(serverPort);
		}
		catch (SocketException ex) {
			PieLogger.error(this.getClass(), "Error starting loop hole punch service!", ex);
		}

		LoopHoleListenerTask listenerTask = beanService.getBean(LoopHoleListenerTask.class);
		listenerTask.setSocket(socket);
		executorService.execute(listenerTask);
	}

	@Override
	public synchronized void send(IBasePieMessage msg, String host, int port) {
		try {
			byte[] bytes = serializerService.serialize(msg);
			PieLogger.info(this.getClass(), String.format("Sending to Host: %s, Port: %s. Data: %s", host, port, new String(bytes)));
			
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(host), port);
			socket.send(packet);
		}
		catch (SerializerServiceException ex) {
			PieLogger.error(this.getClass(), "Error serializing message", ex);
		}
		catch (UnknownHostException ex) {
			PieLogger.error(this.getClass(), "UnknownHostException", ex);
		}
		catch (IOException ex) {
			PieLogger.error(this.getClass(), "IOException", ex);
		}
	}
}
