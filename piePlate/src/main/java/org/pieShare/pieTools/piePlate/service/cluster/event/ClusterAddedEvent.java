/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.piePlate.service.cluster.event;

import java.util.EventObject;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;

/**
 *
 * @author Svetoslav
 */
public class ClusterAddedEvent extends EventObject {
	
	private IClusterService clusterService;

	public ClusterAddedEvent(Object source, IClusterService clusterService) {
		super(source);
		this.clusterService = clusterService;
	}
	
	public IClusterService getClusterService() {
		return this.clusterService;
	}
}
