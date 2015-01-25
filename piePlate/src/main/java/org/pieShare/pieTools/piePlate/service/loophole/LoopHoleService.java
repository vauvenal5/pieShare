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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.pieShare.pieTools.piePlate.model.UdpAddress;
import org.pieShare.pieTools.piePlate.model.message.api.IClusterMessage;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleAckMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleConnectionMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHolePunchMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.RegisterMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;
import org.pieShare.pieTools.piePlate.service.loophole.api.ILoopHoleService;
import org.pieShare.pieTools.piePlate.service.loophole.event.NewLoopHoleConnectionEvent;
import org.pieShare.pieTools.piePlate.service.loophole.event.api.INewLoopHoleConnectionEventListener;
import org.pieShare.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieShare.pieTools.piePlate.service.serializer.exception.SerializerServiceException;
import org.pieShare.pieTools.piePlate.task.LoopHoleAckTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleConnectionTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleListenerTask;
import org.pieShare.pieTools.piePlate.task.LoopHolePuncherTask;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
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
    private IBeanService beanService;
    private IIDService idService;
    private PieExecutorTaskFactory executorFactory;
    private ISerializerService serializerService;
    private HashMap<String, LoopHoleConnectionTask> waitForAckQueue;
    private String clientID;
    private String name;
    private PieExecutorService executorService;
    private IEventBase<INewLoopHoleConnectionEventListener, NewLoopHoleConnectionEvent> newLoopHoleConnectionEvent;

    public LoopHoleService() {

    }

    @PostConstruct
    public void init() {
        this.executorFactory.registerTask(LoopHoleConnectionMessage.class, LoopHoleConnectionTask.class);
        this.executorFactory.registerTask(LoopHolePunchMessage.class, LoopHolePuncherTask.class);
        this.executorFactory.registerTask(LoopHoleAckMessage.class, LoopHoleAckTask.class);

        clientID = idService.getNewID();

        serverPort = 6312;
        serverIP = "server.piesystems.org";

        InetAddress IP = null;
        try {
            IP = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            PieLogger.error(this.getClass(), "Error getting IP Address of client", ex);
        }

        localIP = IP.getHostAddress();

        //ToDo: Get port from port service.
        this.localPort = 1234;
        PieLogger.info(this.getClass(), String.format("LoopHoleService startet at IP: %s, Port: %s.", localIP, localPort));

        try {
            this.socket = new DatagramSocket(localPort);
        } catch (SocketException ex) {
            PieLogger.error(this.getClass(), "Error creating DatagramSocket of client", ex);
        }

        waitForAckQueue = new HashMap<>();

        LoopHoleListenerTask listenerTask = beanService.getBean(LoopHoleListenerTask.class);
        listenerTask.setSocket(socket);
        executorService.execute(listenerTask);

    }

    public void setExecutorService(PieExecutorService executorService) {
        this.executorService = executorService;
    }

    public IEventBase<INewLoopHoleConnectionEventListener, NewLoopHoleConnectionEvent> getNewLoopHoleConnectionEvent() {
        return newLoopHoleConnectionEvent;
    }

    public void setNewLoopHoleConnectionEvent(IEventBase<INewLoopHoleConnectionEventListener, NewLoopHoleConnectionEvent> newLoopHoleConnectionEvent) {
        this.newLoopHoleConnectionEvent = newLoopHoleConnectionEvent;
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
    public synchronized void ackArrived(String fromid) {
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
        PieLogger.info(this.getClass(), String.format("New UPD connection available. Host: %s, Port: %s", host, port));
        UdpAddress address = new UdpAddress();
        address.setHost(host);
        address.setPort(port);
        newLoopHoleConnectionEvent.fireEvent(new NewLoopHoleConnectionEvent(this, address));
    }

    @Override
    public void register() {
        RegisterMessage message = beanService.getBean(RegisterMessage.class);
        message.setPrivateHost(localIP);
        message.setPrivatePort(localPort);
        message.setName(name);
        send(message, serverIP, serverPort);
    }

    @Override
    public synchronized void send(IUdpMessage msg, String host, int port) {
        try {
            msg.setSenderID(this.clientID);
            byte[] bytes = serializerService.serialize(msg);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(host), port);
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
       send(msg, serverIP, serverPort);
    }
}
