/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.model.message;

import org.pieShare.pieTools.piePlate.model.UdpAddress;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;

/**
 *
 * @author RicLeo00
 */
public abstract class UdpMessage extends HeaderMessage implements IUdpMessage {

    private UdpAddress senderAddress;
    private String ID;
    private String localLoppHoleID;
    private String clientLoppHoleID;
    
    @Override
    public void setSenderAddress(UdpAddress senderAddress) {
        this.senderAddress = senderAddress;
    }

    @Override
    public UdpAddress getSenderAddress() {
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
