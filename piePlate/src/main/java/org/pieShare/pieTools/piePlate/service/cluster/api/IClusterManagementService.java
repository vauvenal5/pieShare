/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.api;

import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;

/**
 *
 * @author Svetoslav
 */
public interface IClusterManagementService {

	void sendMessage(IPieMessage message) throws ClusterManagmentServiceException;

	IClusterService connect(String id) throws ClusterManagmentServiceException;
}
