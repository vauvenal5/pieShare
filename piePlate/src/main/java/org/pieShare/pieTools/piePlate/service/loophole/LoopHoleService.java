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
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import javax.inject.Provider;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.RegisterMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;
import org.pieShare.pieTools.piePlate.service.loophole.api.ILoopHoleFactory;
import org.pieShare.pieTools.piePlate.service.loophole.api.ILoopHoleService;
import org.pieShare.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieShare.pieTools.piePlate.service.serializer.exception.SerializerServiceException;
import org.pieShare.pieTools.piePlate.task.LoopHoleConnectionTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleListenerTask;
import org.pieShare.pieTools.pieUtilities.service.idService.api.IIDService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class LoopHoleService implements ILoopHoleService {

    private int localPort;
    private String localIP;
    private DatagramSocket socket = null;
    private String serverIP;
    private int serverPort;
    private IIDService idService;
    private PieExecutorTaskFactory executorFactory;
    private ISerializerService serializerService;
    private HashMap<String, LoopHoleConnectionTask> waitForAckQueue;
    private String localLoopHoleID;
    private String name;
    private PieExecutorService executorService;
	private Provider<LoopHoleListenerTask> loopHoleListenerTaskProvider;
	private Provider<RegisterMessage> registerMessageProvider;

    private LoopHoleListenerTask listenerTask;
    private ILoopHoleFactory loopHoleFactory;
    private boolean localLoopHoleComplete;
    private boolean clientLoopHoleComplete;
    private InetSocketAddress clientAddress;

    public LoopHoleService() {

        serverPort = 6312;
        serverIP = "server.piesystems.org";

        InetAddress IP = null;
        try {
            IP = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            PieLogger.error(this.getClass(), "Error getting IP Address of client", ex);
        }

        localIP = IP.getHostAddress();

        //Will be set by setter from factory
        this.localPort = -1;
        localLoopHoleComplete = false;
        clientLoopHoleComplete = false;
    }

    @Override
    public void init() {
        PieLogger.info(this.getClass(), String.format("LoopHoleService startet at IP: %s, Port: %s.", localIP, localPort));

        try {
            this.socket = new DatagramSocket(localPort);
        } catch (SocketException ex) {
            PieLogger.error(this.getClass(), "Error creating DatagramSocket of client", ex);
        }

        localLoopHoleID = idService.getNewID();

        waitForAckQueue = new HashMap<>();

        listenerTask = this.loopHoleListenerTaskProvider.get();
        listenerTask.setSocket(socket);
        executorService.execute(listenerTask);

        register();
    }

    @Override
    public void setLocalPort(int port) {
        this.localPort = port;
    }

    public void setExecutorService(PieExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setLoopHoleFactory(ILoopHoleFactory loopHoleFactory) {
        this.loopHoleFactory = loopHoleFactory;
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
    public String getLocalLoopID() {
        return this.localLoopHoleID;
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

	public void setLoopHoleListenerTaskProvider(Provider<LoopHoleListenerTask> loopHoleListenerTaskProvider) {
		this.loopHoleListenerTaskProvider = loopHoleListenerTaskProvider;
	}

	public void setRegisterMessageProvider(Provider<RegisterMessage> registerMessageProvider) {
		this.registerMessageProvider = registerMessageProvider;
	}

    @Override
    public synchronized void ackArrived(String fromid) {
        if (waitForAckQueue.containsKey(fromid)) {
            waitForAckQueue.get(fromid).ackArrived();
            removeTaskFromAckWaitQueue(fromid);
        }
    }

    private synchronized void loopHoleComplete() {
            if (clientLoopHoleComplete && localLoopHoleComplete) {
                listenerTask.stop();
                socket.close();
                loopHoleFactory.addLocalUsedPort(localPort);
                loopHoleFactory.newClientAvailable(clientAddress, socket);
            }
    }

    @Override
    public void clientCompletedLoopHole() {
        clientLoopHoleComplete = true;
        loopHoleComplete();
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
    public void newClientAvailable(InetSocketAddress address) {
        clientAddress = address;
        localLoopHoleComplete = true;
        loopHoleComplete();
    }

    @Override
    public void register() {
        RegisterMessage message = this.registerMessageProvider.get();
        message.setPrivateHost(localIP);
        message.setPrivatePort(localPort);
        message.setName(name);
        message.setLocalLoopID(localLoopHoleID);
        loopHoleFactory.insertLoopHoleService(message.getLocalLoopID(), this);
        loopHoleFactory.sendToServer(socket, message);
    }

    @Override
    public synchronized void send(IUdpMessage msg, InetSocketAddress address) {
        try {
            msg.setSenderID(loopHoleFactory.getClientID());
            byte[] bytes = serializerService.serialize(msg);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address.getAddress(), address.getPort());
            socket.send(packet);

            Thread.sleep(500);
        } catch (SerializerServiceException ex) {
            PieLogger.error(this.getClass(), "Error serializing message", ex);
        } catch (UnknownHostException ex) {
            PieLogger.error(this.getClass(), "UnknownHostException", ex);
        } catch (IOException ex) {
            PieLogger.error(this.getClass(), "IOException", ex);
        } catch (InterruptedException ex) {
            PieLogger.error(this.getClass(), "InterruptedException", ex);
        }
    }

    @Override
    public void sendToServer(IUdpMessage msg) {
        this.loopHoleFactory.sendToServer(socket, msg);
    }
}
