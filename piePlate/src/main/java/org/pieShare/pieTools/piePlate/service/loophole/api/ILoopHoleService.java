/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.loophole.api;

import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.task.LoopHoleConnectionTask;

/**
 *
 * @author Richard
 */
public interface ILoopHoleService {

	void register();

	void ackArrived(String fromid);

	void removeTaskFromAckWaitQueue(String id);

	void addInWaitFromAckQueu(String id, LoopHoleConnectionTask task);

	void newClientAvailable(String host, int port);

	String getClientID();

	String getName();

	void setName(String name);

	void send (IPieMessage msg, String host, int port);
}
