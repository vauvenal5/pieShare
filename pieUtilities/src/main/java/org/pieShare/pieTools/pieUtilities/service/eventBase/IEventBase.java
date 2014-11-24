/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.eventBase;

import java.util.EventObject;

/**
 *
 * @author Svetoslav
 */
public interface IEventBase<L extends IEventListener<E>, E extends EventObject> {
	public void addEventListener(L listener);
	
	public void removeEventListener(L listener);
	
	public void fireEvent(E event);
}
