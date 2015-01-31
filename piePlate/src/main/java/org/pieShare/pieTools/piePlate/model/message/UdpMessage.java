/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.model.message;

import java.net.InetSocketAddress;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;

/**
 *
 * @author RicLeo00
 */
public abstract class UdpMessage extends HeaderMessage implements IUdpMessage {

    private String senderHost;
    private int senderPort;
    private String ID;
    private String localLoppHoleID;
    private String clientLoppHoleID;

    public String getSenderHost() {
        return senderHost;
    }

    public void setSenderHost(String senderHost) {
        this.senderHost = senderHost;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public void setSenderPort(int senderPort) {
        this.senderPort = senderPort;
    }

    @Override
    public String getSenderID() {
        return this.ID;
    }

    @Override
    public void setSenderID(String ID) {
        this.ID = ID;
    }

    @Override
    public String getLocalLoopID() {
        return localLoppHoleID;
    }

    @Override
    public void setLocalLoopID(String ID) {
        this.localLoppHoleID = ID;
    }

    @Override
    public String getClientLocalLoopID() {
        return this.clientLoppHoleID;
    }

    @Override
    public void setClientLocalLoopID(String ID) {
        this.clientLoppHoleID = ID;
    }
}
