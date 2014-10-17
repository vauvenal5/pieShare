/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.task;

import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEvent;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

/**
 *
 * @author Svetoslav
 */
public abstract class PieEventTaskBase<T extends IPieEvent> implements IPieEventTask<T>{

	protected T msg;
	
	@Override
	public void setMsg(T msg) {
		this.msg = msg;
	}
}
