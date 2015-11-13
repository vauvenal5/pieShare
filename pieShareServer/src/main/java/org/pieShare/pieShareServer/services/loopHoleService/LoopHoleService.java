/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services.loopHoleService;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import org.pieShare.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieShare.pieTools.piePlate.service.serializer.exception.SerializerServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieShareServer.services.loopHoleService.api.ILoopHoleService;
import org.pieShare.pieShareServer.services.loopHoleService.api.IUserPersistanceService;
import org.pieShare.pieShareServer.tasks.LoopHoleListenerTask;
import org.pieShare.pieShareServer.tasks.LoopHoleServerAckTask;
import org.pieShare.pieShareServer.tasks.RegisterTask;
import org.pieShare.pieShareServer.tasks.WaitForAckFromClientTask;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleAckMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.RegisterMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;

/**
 *
 * @author Richard
 */
public class LoopHoleService implements ILoopHoleService {

    private int serverPort;
    private IBeanService beanService;
    private DatagramSocket socket;
    private PieExecutorService executorService;
    private IUserPersistanceService userPersistanceService;
    private ISerializerService serializerService;
    private PieExecutorTaskFactory executorFactory;
    private HashMap<String, WaitForAckFromClientTask> waitForAckQueue;

    public void setExecutorFactory(PieExecutorTaskFactory executorFactory) {
        this.executorFactory = executorFactory;
    }

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

    @Override
    public synchronized void ackArrived(String ID) {
        if (waitForAckQueue.containsKey(ID)) {
            waitForAckQueue.get(ID).ackArrived();
        }
        waitForAckQueue.remove(ID);
    }

    @PostConstruct
    public void init() {
        waitForAckQueue = new HashMap<>();

        PieLogger.info(this.getClass(), "Set up Loop Hole Service!");
        serverPort = 6312;
        try {
            socket = new DatagramSocket(serverPort);
        } catch (SocketException ex) {
            PieLogger.error(this.getClass(), "Error starting loop hole punch service!", ex);
        }

        LoopHoleListenerTask listenerTask = beanService.getBean(LoopHoleListenerTask.class);
        listenerTask.setSocket(socket);
        executorService.execute(listenerTask);
    }

    @Override
    public synchronized void send(IUdpMessage msg, InetSocketAddress address) {
        try {
            byte[] bytes = serializerService.serialize(msg);

            PieLogger.info(this.getClass(), "Add new AckWaitTask.");
            WaitForAckFromClientTask waitTask = beanService.getBean(WaitForAckFromClientTask.class);
            waitTask.setMmsgToSend(msg);
            waitTask.setUdpAddress(address);
            waitForAckQueue.put(msg.getLocalLoopID(), waitTask);
            executorService.execute(waitTask);
            PieLogger.info(this.getClass(), String.format("Sending to Host: %s, Port: %s. Data: %s", address.getAddress().getHostAddress(), address.getPort(), new String(bytes)));

            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(address.getAddress().getHostAddress()), address.getPort());
            socket.send(packet);
            Thread.sleep(1000);
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

}
