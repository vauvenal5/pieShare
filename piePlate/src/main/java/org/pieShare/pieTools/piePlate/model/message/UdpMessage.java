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

    private InetSocketAddress senderAddress;
    private String ID;
    private String localLoppHoleID;
    private String clientLoppHoleID;
    
    @Override
    public void setSenderAddress(InetSocketAddress senderAddress) {
        this.senderAddress = senderAddress;
    }

    @Override
    public InetSocketAddress getSenderAddress() {
        return senderAddress;
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
