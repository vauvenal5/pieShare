/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services.model;

import org.pieShare.pieTools.piePlate.model.UdpAddress;

/**
 *
 * @author Richard
 */
public class User {

    private String name;
    private String clientID;
    private UdpAddress privateAddress;
    private UdpAddress publicAddress;
    private String loopHoleID;
    private String connectedTo = null;
    
    public String getName() {
        return name;
    }

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

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return clientID;
    }

    public void setId(String id) {
        this.clientID = id;
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
