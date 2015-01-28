/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api;

import org.pieShare.pieTools.piePlate.model.IPieAddress;
import org.pieShare.pieTools.piePlate.model.UdpAddress;
import org.pieShare.pieTools.piePlate.model.message.api.IClusterMessage;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;

/**
 *
 * @author Richard
 */
public interface IUdpMessage extends IPieMessage {

	void setSenderAddress(UdpAddress senderAddress);

	UdpAddress getSenderAddress();
        
        String getSenderID();
        
        void setSenderID(String ID);
        
        String getLocalLoopID();
        
        void setLocalLoopID(String ID);
}
