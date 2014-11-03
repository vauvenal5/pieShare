/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.event;

import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieCallable;

/**
 *
 * @author Svetoslav
 */
public interface IPieCallbackEvent<C extends IPieCallable> extends IPieEvent {
	void setCallback(C callback);
	C getCallback();
}
