/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.api;

import java.util.Map;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
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

	void sendMessage(IPieMessage message, EncryptedPassword key) throws ClusterManagmentServiceException;

	void sendMessage(IPieMessage message, String cloudName, EncryptedPassword key) throws ClusterManagmentServiceException;

	IClusterService connect(String id) throws ClusterManagmentServiceException;

	void disconnect(String id) throws ClusterServiceException;

	void diconnectAll() throws ClusterManagmentServiceException;

	Map<String, IClusterService> getClusters();
}
