/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services.loopHoleService.api;

import java.net.InetSocketAddress;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;

/**
 *
 * @author Richard
 */
public interface ILoopHoleService {

	void send(IUdpMessage msg, InetSocketAddress address);
        
        void ackArrived(String ID);
}
