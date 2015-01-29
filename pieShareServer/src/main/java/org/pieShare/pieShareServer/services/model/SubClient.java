/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services.model;

import org.pieShare.pieTools.piePlate.model.UdpAddress;

/**
 *
 * @author RicLeo00
 */
public class SubClient {

    private UdpAddress privateAddress;
    private UdpAddress publicAddress;
    private String loopHoleID;
    private String connectedTo = null;

    public String getConnectedTo() {
        return connectedTo;
    }

    public void setConnectedTo(String connectedTo) {
        this.connectedTo = connectedTo;
    }

    public String getLoopHoleID() {
        return loopHoleID;
    }

    public void setLoopHoleID(String loopHoleID) {
        this.loopHoleID = loopHoleID;
    }

    public UdpAddress getPrivateAddress() {
        return privateAddress;
    }

    public void setPrivateAddress(UdpAddress privateAddress) {
        this.privateAddress = privateAddress;
    }

    public UdpAddress getPublicAddress() {
        return publicAddress;
    }

    public void setPublicAddress(UdpAddress publicAddress) {
        this.publicAddress = publicAddress;
    }
}
