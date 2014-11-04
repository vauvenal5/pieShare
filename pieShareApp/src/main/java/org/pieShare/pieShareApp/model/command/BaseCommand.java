/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.command;

import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieCallable;

/**
 *
 * @author Richard
 */
public abstract class BaseCommand<C extends IPieCallable> {
	
	private C callback;

	public C getCallback() {
		return callback;
	}

	public void setCallback(C callback) {
		this.callback = callback;
	}
}
