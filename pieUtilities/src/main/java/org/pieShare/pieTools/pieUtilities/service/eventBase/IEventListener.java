/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.eventBase;

import java.util.EventListener;
import java.util.EventObject;

/**
 *
 * @author Svetoslav
 */
public interface IEventListener<E extends EventObject> extends EventListener {
	public void handleObject(E event);
}
