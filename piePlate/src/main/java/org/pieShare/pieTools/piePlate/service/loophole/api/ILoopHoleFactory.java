/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.loophole.api;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;
import org.pieShare.pieTools.piePlate.service.loophole.event.NewLoopHoleConnectionEvent;
import org.pieShare.pieTools.piePlate.service.loophole.event.api.INewLoopHoleConnectionEventListener;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;

/**
 *
 * @author RicLeo00
 */
public interface ILoopHoleFactory {

    void initializeNewLoopHole();
   
    void setName(String name);

    void sendToServer(DatagramSocket socket, IUdpMessage msg);

    ILoopHoleService getLoopHoleService(String clientID);

    void insertLoopHoleService(String ID, ILoopHoleService service);

    String getClientID();
    
    void newClientAvailable(InetSocketAddress address, DatagramSocket socket);
    
    IEventBase<INewLoopHoleConnectionEventListener, NewLoopHoleConnectionEvent> getNewLoopHoleConnectionEvent();
}
