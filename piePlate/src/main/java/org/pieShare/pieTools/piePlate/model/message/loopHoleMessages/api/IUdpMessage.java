/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api;

import java.net.InetSocketAddress;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;

/**
 *
 * @author Richard
 */
public interface IUdpMessage extends IPieMessage {

    String getSenderHost();

    void setSenderHost(String senderHost);

    int getSenderPort();

    void setSenderPort(int senderPort);

    String getSenderID();

    void setSenderID(String ID);

    String getLocalLoopID();

    void setLocalLoopID(String ID);

    String getClientLocalLoopID();

    void setClientLocalLoopID(String ID);
}
