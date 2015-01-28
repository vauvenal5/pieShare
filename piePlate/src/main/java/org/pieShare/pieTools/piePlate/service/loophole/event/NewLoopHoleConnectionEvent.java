/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.loophole.event;

import java.net.DatagramSocket;
import java.util.EventObject;
import org.pieShare.pieTools.piePlate.model.UdpAddress;

/**
 *
 * @author RicLeo00
 */
public class NewLoopHoleConnectionEvent extends EventObject {

    private UdpAddress address;
    private DatagramSocket socket;

    public UdpAddress getAddress() {
        return address;
    }

    public void setAddress(UdpAddress address) {
        this.address = address;
    }

    public NewLoopHoleConnectionEvent(Object source, UdpAddress address, DatagramSocket socket) {
        super(source);
        this.address = address;
        this.socket = socket;
    }

}
