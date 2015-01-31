/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import org.bouncycastle.util.Arrays;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;
import org.pieShare.pieTools.piePlate.service.serializer.api.ISerializerService;
import org.pieShare.pieTools.piePlate.service.serializer.exception.SerializerServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorTaskFactoryException;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class LoopHoleListenerTask implements IPieTask {

    private DatagramSocket socket;
    private boolean run;
    private ISerializerService serializerService;
    private IExecutorService excuterService;

    public LoopHoleListenerTask() {
        this.run = true;
    }

    public void setSerializerService(ISerializerService serializerService) {
        this.serializerService = serializerService;
    }

    public void setExcuterService(IExecutorService excuterService) {
        this.excuterService = excuterService;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        PieLogger.info(this.getClass(), "Listener Started!");
        while (run) {

            byte[] bytes = new byte[1024];
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

            try {
                socket.receive(packet);
                bytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
                IUdpMessage msg = (IUdpMessage) serializerService.deserialize(bytes);
                msg.setSenderHost(packet.getAddress().getHostAddress());
                msg.setSenderPort(packet.getPort());
                
                excuterService.handlePieEvent(msg);
            } catch (IOException ex) {
                PieLogger.error(this.getClass(), "Error receiving message.", ex);
            } catch (PieExecutorTaskFactoryException ex) {
                PieLogger.error(this.getClass(), "Error receiving message.", ex);
            } catch (SerializerServiceException ex) {
                PieLogger.error(this.getClass(), "Error receiving message.", ex);
            }
        }
    }

    public void stop() {
        run = false;
    }

}
