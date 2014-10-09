/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.api;

import java.util.Map;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterAddedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterAddedListener;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;

/**
 *
 * @author Svetoslav
 */
public interface IClusterManagementService {

	IEventBase<IClusterAddedListener, ClusterAddedEvent> getClusterAddedEventBase();
	
	void sendMessage(IPieMessage message) throws ClusterManagmentServiceException;
	
	void sendMessage(IPieMessage message, String cloudName) throws ClusterManagmentServiceException;

	IClusterService connect(String id) throws ClusterManagmentServiceException;
	
	void diconnectAll() throws ClusterManagmentServiceException;
	
	Map<String, IClusterService> getClusters();
}
