/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.piePlate.service.cluster.event;

import java.util.EventObject;

/**
 *
 * @author Svetoslav
 */
public class ClusterRemovedEvent extends EventObject {

	public ClusterRemovedEvent(Object source) {
		super(source);
	}
	
}
