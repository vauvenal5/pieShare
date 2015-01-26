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
import javax.annotation.PostConstruct;
import org.pieShare.pieTools.piePlate.model.UdpAddress;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.RegisterMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;
import org.pieShare.pieTools.piePlate.service.loophole.api.ILoopHoleFactory;
import org.pieShare.pieTools.piePlate.service.loophole.api.ILoopHoleService;
import org.pieShare.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieShare.pieTools.piePlate.service.serializer.exception.SerializerServiceException;
import org.pieShare.pieTools.piePlate.task.LoopHoleListenerTask;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.idService.api.IIDService;
import org.pieShare.pieTools.pieUtilities.service.networkService.api.IUdpPortService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author RicLeo00
 */
public class LoopHoleFactory implements ILoopHoleFactory {

    private final HashMap<String, LoopHoleService> loopQueue;
    private final HashMap<Integer, DatagramSocket> newConnections;
    private IUdpPortService udpPortService;
    private int nextUdpPort;
    private IBeanService beanService;
    private String clientID;
    private IIDService idService;
    private ISerializerService serializerService;
    private final String serverIP;
    private final int serverPort;
    private String name;
    private String localIP;
    private PieExecutorTaskFactory executorFactory;
    private PieExecutorService executorService;

    public LoopHoleFactory() {
        loopQueue = new HashMap<>();
        newConnections = new HashMap<>();

        nextUdpPort = 1234;
        serverPort = 6312;

        serverIP = "server.piesystems.org";

        InetAddress IP = null;
        try {
            IP = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            PieLogger.error(this.getClass(), "Error getting IP Address of client", ex);
        }

        localIP = IP.getHostAddress();
    }

    @PostConstruct
    public void init() {
        clientID = idService.getNewID();
    }

    public void setExecutorFactory(PieExecutorTaskFactory executorFactory) {
        this.executorFactory = executorFactory;
    }

    public void setExecutorService(PieExecutorService executorService) {
        this.executorService = executorService;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSerializerService(ISerializerService serializerService) {
        this.serializerService = serializerService;
    }

    public void setIdService(IIDService idService) {
        this.idService = idService;
    }

    public void setBeanService(IBeanService beanService) {
        this.beanService = beanService;
    }

    public void setUdpPortService(IUdpPortService udpPortService) {
        this.udpPortService = udpPortService;
    }

    public void initializeNewLoopHole() {
        
        ILoopHoleService loopHoleService = beanService.getBean(LoopHoleService.class);
        nextUdpPort = udpPortService.getNewPortFrom(nextUdpPort);

        DatagramSocket socket;
        try {
            socket = new DatagramSocket(nextUdpPort);
        } catch (SocketException ex) {
            PieLogger.error(this.getClass(), String.format("Error ceating DatagramSocket at port: %s,", nextUdpPort), ex);
            return;
        }

        newConnections.put(nextUdpPort, socket);

        LoopHoleListenerTask task = beanService.getBean(LoopHoleListenerTask.class);
        task.setSocket(socket);
        executorService.execute(task);
        
        register(socket, nextUdpPort);
    }

    private void register(DatagramSocket socket, int newPort) {
        RegisterMessage message = beanService.getBean(RegisterMessage.class);
        message.setPrivateHost(localIP);
        message.setPrivatePort(newPort);
        message.setName(name);
        UdpAddress address = new UdpAddress();
        address.setHost(localIP);
        address.setPort(newPort);
        send(socket, message, address);
    }

    @Override
    public synchronized void send(DatagramSocket socket, IUdpMessage msg, UdpAddress address) {
        try {
            msg.setSenderID(this.clientID);
            byte[] bytes = serializerService.serialize(msg);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(address.getHost()), address.getPort());
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

//    @Override
//    public void sendToServer(DatagramSocket socket,IUdpMessage msg) {
//        send(msg, serverIP, serverPort);
//    }
    @Override
    public void sendToServer(IUdpMessage msg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
