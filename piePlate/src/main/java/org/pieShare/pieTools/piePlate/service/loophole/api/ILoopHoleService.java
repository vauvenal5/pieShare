/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.loophole.api;

import java.net.InetSocketAddress;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;
import org.pieShare.pieTools.piePlate.task.LoopHoleConnectionTask;

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

    void newClientAvailable(InetSocketAddress address);

    String getLocalLoopID();

    String getName();

    void setName(String name);

    void send(IUdpMessage msg, InetSocketAddress address);

    void sendToServer(IUdpMessage msg);

    void clientCompletedLoopHole();
}
