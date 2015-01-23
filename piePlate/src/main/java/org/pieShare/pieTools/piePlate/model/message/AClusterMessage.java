/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.model.message;

import org.pieShare.pieTools.piePlate.model.IPieAddress;
import org.pieShare.pieTools.piePlate.model.message.api.IClusterMessage;

/**
 *
 * @author RicLeo00
 */
public abstract class AClusterMessage extends HeaderMessage implements IClusterMessage {

    private IPieAddress address;
    
    @Override
    public IPieAddress getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(IPieAddress address) {
        this.address = address;
    }
}
