/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.loophole.event;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.EventObject;

/**
 *
 * @author RicLeo00
 */
public class NewLoopHoleConnectionEvent extends EventObject {

    private InetSocketAddress address;
    private DatagramSocket socket;

    public NewLoopHoleConnectionEvent(Object source, InetSocketAddress address, DatagramSocket socket) {
        super(source);
        this.address = address;
        this.socket = socket;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

}
