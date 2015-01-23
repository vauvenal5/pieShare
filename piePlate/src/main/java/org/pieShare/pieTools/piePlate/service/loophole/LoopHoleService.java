/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.loophole;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import org.pieShare.pieTools.piePlate.model.message.api.IClusterMessage;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleConnectionMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHolePunchMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.RegisterMessage;
import org.pieShare.pieTools.piePlate.service.loophole.api.ILoopHoleService;
import org.pieShare.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieShare.pieTools.piePlate.service.serializer.exception.SerializerServiceException;
import org.pieShare.pieTools.piePlate.task.LoopHoleConnectionTask;
import org.pieShare.pieTools.piePlate.task.LoopHolePuncherTask;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.idService.api.IIDService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class LoopHoleService implements ILoopHoleService {

	private final int localPort;
	private final String localIP;
	private DatagramSocket socket = null;
	private final String serverIP;
	private final int serverPort;
	private IBeanService beanService;
	private IIDService idService;
	private PieExecutorTaskFactory executorFactory;
	private ISerializerService serializerService;
	private HashMap<String, LoopHoleConnectionTask> waitForAckQueue;
	private String clientID;
	private String name;

	public LoopHoleService() {
		clientID = idService.getNewID();

		serverPort = 6312;
		serverIP = "server.piesystems.org";

		InetAddress IP = null;
		try {
			IP = InetAddress.getLocalHost();
		}
		catch (UnknownHostException ex) {
			PieLogger.error(this.getClass(), "Error getting IP Address of client", ex);
		}

		localIP = IP.getHostAddress();

		//ToDo: Get port from port service.
		this.localPort = 1234;
		PieLogger.info(this.getClass(), String.format("LoopHoleService startet at IP: %s, Port: %s.", localIP, localPort));

		try {
			this.socket = new DatagramSocket(localPort);
		}
		catch (SocketException ex) {
			PieLogger.error(this.getClass(), "Error creating DatagramSocket of client", ex);
		}

		waitForAckQueue = new HashMap<>();
	}

	public void init() {
		this.executorFactory.registerTask(LoopHoleConnectionMessage.class, LoopHoleConnectionTask.class);
		this.executorFactory.registerTask(LoopHolePunchMessage.class, LoopHolePuncherTask.class);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getClientID() {
		return clientID;
	}

	public void setSerializerService(ISerializerService serializerService) {
		this.serializerService = serializerService;
	}

	public void setExecutorFactory(PieExecutorTaskFactory executorFactory) {
		this.executorFactory = executorFactory;
	}

	public void setIdService(IIDService idService) {
		this.idService = idService;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	@Override
	public void ackArrived(String fromid) {
		if (waitForAckQueue.containsKey(fromid)) {
			waitForAckQueue.get(fromid).ackArrived();
			removeTaskFromAckWaitQueue(fromid);
		}
	}

	@Override
	public void removeTaskFromAckWaitQueue(String id) {
		waitForAckQueue.remove(id);
	}

	@Override
	public void addInWaitFromAckQueu(String id, LoopHoleConnectionTask task) {
		waitForAckQueue.put(id, task);
	}

	@Override
	public void newClientAvailable(String host, int port) {
		//ToDo: Throw event
	}

	@Override
	public void register() {
		RegisterMessage message = beanService.getBean(RegisterMessage.class);
		message.setPrivateHost(localIP);
		message.setPrivatePort(localPort);
		message.setName(name);
		message.setId(clientID);
		send(message, serverIP, serverPort);
	}

	@Override
	public synchronized void send(IPieMessage msg, String host, int port) {
		try {
			byte[] bytes = serializerService.serialize(msg);
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
