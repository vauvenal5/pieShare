/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.api;

import java.util.Map;
import org.pieShare.pieTools.piePlate.model.message.api.IClusterMessage;
import org.pieShare.pieTools.piePlate.service.channel.api.IIncomingChannel;
import org.pieShare.pieTools.piePlate.service.channel.api.IOutgoingChannel;
import org.pieShare.pieTools.piePlate.service.channel.api.ITwoWayChannel;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterAddedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterRemovedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterAddedListener;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterRemovedListener;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;

/**
 *
 * @author Svetoslav
 */
public interface IClusterManagementService {

	IEventBase<IClusterRemovedListener, ClusterRemovedEvent> getClusterRemovedEventBase();

	IEventBase<IClusterAddedListener, ClusterAddedEvent> getClusterAddedEventBase();

	void sendMessage(IClusterMessage message) throws ClusterManagmentServiceException;

	void reconnectAll() throws ClusterManagmentServiceException;
	
	void disconnect(String id) throws ClusterServiceException;

	void diconnectAll() throws ClusterManagmentServiceException;

	Map<String, IClusterService> getClusters();
	
	void registerChannel(String clusterId, IIncomingChannel channel) throws ClusterManagmentServiceException;
	
	void registerChannel(String clusterId, IOutgoingChannel channel) throws ClusterManagmentServiceException;
	
	void registerChannel(String clusterId, ITwoWayChannel channel) throws ClusterManagmentServiceException;
}
