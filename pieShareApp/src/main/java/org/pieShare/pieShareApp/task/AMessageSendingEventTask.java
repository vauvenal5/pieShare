/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.task;

import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.event.IPieEvent;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;

/**
 *
 * @author Svetoslav
 */
public abstract class AMessageSendingEventTask<T extends IPieEvent> extends AMessageSendingTask implements IPieEventTask<T>{

	protected T msg;

	@Override
	public void setEvent(T msg) {
		this.msg = msg;
	}
	
}
