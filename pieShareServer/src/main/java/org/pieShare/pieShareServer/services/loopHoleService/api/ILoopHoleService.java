/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services.loopHoleService.api;

import org.pieShare.pieTools.piePlate.model.message.api.IBasePieMessage;

/**
 *
 * @author Richard
 */
public interface ILoopHoleService {

	void send(IBasePieMessage msg, String host, int port);
}