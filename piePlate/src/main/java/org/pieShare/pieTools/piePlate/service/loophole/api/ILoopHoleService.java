/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.loophole.api;

import java.net.DatagramSocket;
import org.pieShare.pieTools.piePlate.model.UdpAddress;
import org.pieShare.pieTools.piePlate.model.message.api.IClusterMessage;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;
import org.pieShare.pieTools.piePlate.task.LoopHoleConnectionTask;
import org.pieShare.pieTools.piePlate.task.LoopHoleListenerTask;

/**
 *
 * @author Richard
 */
public interface ILoopHoleService {

    void init();

    void setLocalPort(int port);

    void register();

    void ackArrived(String fromid);

    void removeTaskFromAckWaitQueue(String id);

    void addInWaitFromAckQueu(String id, LoopHoleConnectionTask task);

    void newClientAvailable(UdpAddress address);

    String getLocalLoopID();

    String getName();

    void setName(String name);

    void send(IUdpMessage msg, String host, int port);

    void sendToServer(IUdpMessage msg);
}
