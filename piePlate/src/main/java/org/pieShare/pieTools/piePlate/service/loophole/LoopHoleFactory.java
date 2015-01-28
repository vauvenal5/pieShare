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
import java.net.UnknownHostException;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import org.pieShare.pieTools.piePlate.model.UdpAddress;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleAckMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleConnectionMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHolePunchMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;
import org.pieShare.pieTools.piePlate.service.loophole.api.ILoopHoleFactory;
import org.pieShare.pieTools.piePlate.service.loophole.api.ILoopHoleService;
import org.pieShare.pieTools.piePlate.service.loophole.event.NewLoopHoleConnectionEvent;
import org.pieShare.pieTools.piePlate.service.loophole.event.api.INewLoopHoleConnectionEventListener;
import org.pieShare.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieShare.pieTools.piePlate.service.serializer.exception.SerializerServiceException;
import org.pieShare.pieTools.piePlate.task.LoopHoleAckTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleConnectionTask;
import org.pieShare.pieTools.piePlate.task.LoopHolePuncherTask;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
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

    private final HashMap<String, ILoopHoleService> loopQueue;
    private IUdpPortService udpPortService;
    private int nextUdpPort;
    private IBeanService beanService;
    private String clientID;
    private IIDService idService;
    private ISerializerService serializerService;
    private final UdpAddress serverAddress;
    private String name;
    private PieExecutorTaskFactory executorFactory;
    private PieExecutorService executorService;

    private IEventBase<INewLoopHoleConnectionEventListener, NewLoopHoleConnectionEvent> newLoopHoleConnectionEvent;

    public LoopHoleFactory() {
        this.executorFactory.registerTask(LoopHoleConnectionMessage.class, LoopHoleConnectionTask.class);
        this.executorFactory.registerTask(LoopHolePunchMessage.class, LoopHolePuncherTask.class);
        this.executorFactory.registerTask(LoopHoleAckMessage.class, LoopHoleAckTask.class);

        loopQueue = new HashMap<>();

        nextUdpPort = 1234;

        serverAddress = new UdpAddress();
        serverAddress.setPort(6312);
        serverAddress.setHost("server.piesystems.org");
    }

    @PostConstruct
    public void init() {
        clientID = idService.getNewID();
    }

    @Override
    public IEventBase<INewLoopHoleConnectionEventListener, NewLoopHoleConnectionEvent> getNewLoopHoleConnectionEvent() {
        return newLoopHoleConnectionEvent;
    }

    public void setNewLoopHoleConnectionEvent(IEventBase<INewLoopHoleConnectionEventListener, NewLoopHoleConnectionEvent> newLoopHoleConnectionEvent) {
        this.newLoopHoleConnectionEvent = newLoopHoleConnectionEvent;
    }

    @Override
    public synchronized void newClientAvailable(UdpAddress address, DatagramSocket socket) {
        PieLogger.info(this.getClass(), String.format("New UPD connection available. Host: %s, Port: %s", address.getHost(), address.getPort()));
        newLoopHoleConnectionEvent.fireEvent(new NewLoopHoleConnectionEvent(this, address, socket));
    }

    @Override
    public String getClientID() {
        return clientID;
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
        loopHoleService.setLocalPort(nextUdpPort);
        loopHoleService.setName(name);

        loopHoleService.init();
    }

    @Override
    public void sendToServer(DatagramSocket socket, IUdpMessage msg) {
        send(socket, msg, serverAddress);
    }

    private synchronized void send(DatagramSocket socket, IUdpMessage msg, UdpAddress address) {
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

    @Override
    public ILoopHoleService getLoopHoleService(String clientID) {
        return loopQueue.get(clientID);
    }

    @Override
    public void insertLoopHoleService(String ID, ILoopHoleService service) {
        loopQueue.put(ID, service);
    }
}
