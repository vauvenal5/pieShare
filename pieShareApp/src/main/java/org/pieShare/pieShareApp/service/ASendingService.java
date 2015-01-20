/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service;

import org.pieShare.pieShareApp.service.factoryService.IMessageFactoryService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;

/**
 *
 * @author Svetoslav Videnov
 */
public class ASendingService {
    protected IClusterManagementService clusterManagementService;
    protected IMessageFactoryService messageFactoryService;

    public void setClusterManagementService(IClusterManagementService clusterManagementService) {
        this.clusterManagementService = clusterManagementService;
    }

    public void setMessageFactoryService(IMessageFactoryService messageFactoryService) {
        this.messageFactoryService = messageFactoryService;
    }
}
