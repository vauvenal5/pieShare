/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.loophole.api;

import java.net.DatagramSocket;
import org.pieShare.pieTools.piePlate.model.UdpAddress;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;

/**
 *
 * @author RicLeo00
 */
public interface ILoopHoleFactory {

    void send(DatagramSocket socket, IUdpMessage msg, UdpAddress address);

    void sendToServer(IUdpMessage msg);
}
