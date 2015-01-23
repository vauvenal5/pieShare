/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.model.message;

import org.pieShare.pieTools.piePlate.model.IPieAddress;
import org.pieShare.pieTools.piePlate.model.UdpAddress;
import org.pieShare.pieTools.piePlate.model.message.api.IClusterMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;

/**
 *
 * @author RicLeo00
 */
public abstract class UdpMessage extends HeaderMessage implements IUdpMessage {

    private UdpAddress senderAddress;

    @Override
    public void setSenderAddress(UdpAddress senderAddress) {
        this.senderAddress = senderAddress;
    }

    @Override
    public UdpAddress getSenderAddress() {
        return senderAddress;
    }

}
