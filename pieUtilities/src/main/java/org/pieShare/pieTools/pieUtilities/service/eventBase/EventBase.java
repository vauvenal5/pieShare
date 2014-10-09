/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.eventBase;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 *
 * @author Svetoslav
 */
public class EventBase<L extends IEventListener<E>, E extends EventObject> implements IEventBase<L,E>{
	
	private List<L> listeners;
	
	public EventBase() {
		this.listeners = new ArrayList();
	}

	@Override
	public void addEventListener(L listener) {
		if(!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	@Override
	public void removeEventListener(L listener) {
		if(this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}

	@Override
	public void fireEvent(E event) {
		this.listeners.stream().forEach((listener) -> {
			listener.handleObject(event);
		});
	}

}
