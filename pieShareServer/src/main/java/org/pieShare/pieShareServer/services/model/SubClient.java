/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services.model;

import java.net.InetSocketAddress;

/**
 *
 * @author RicLeo00
 */
public class SubClient {

    private InetSocketAddress privateAddress;
    private InetSocketAddress publicAddress;
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

    public InetSocketAddress getPrivateAddress() {
        return privateAddress;
    }

    public void setPrivateAddress(InetSocketAddress privateAddress) {
        this.privateAddress = privateAddress;
    }

    public InetSocketAddress getPublicAddress() {
        return publicAddress;
    }

    public void setPublicAddress(InetSocketAddress publicAddress) {
        this.publicAddress = publicAddress;
    }
}
