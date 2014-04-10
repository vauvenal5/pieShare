/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieCeption.service.action;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieTools.pieCeption.service.core.api.IPieCeptionService;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.pieUtilities.service.commandParser.api.IAction;

/**
 *
 * @author Svetoslav
 */
public abstract class CommandAction implements IAction {

    private IPieCeptionService pieCeptionService;
    
    private IClusterService clusterService;
    
    public void setPieCeptionService(IPieCeptionService service) {
        this.pieCeptionService = service;
    }
    
    public void setClusterService(IClusterService service) {
        this.clusterService = service;
    }
    
    protected void commitPieMessage(IPieMessage message) {
        //todo-sv: rething this
        try {
            //this.pieCeptionService.handlePieMessage(message);
            this.clusterService.sendMessage(message);
        } catch (ClusterServiceException ex) {
            ex.printStackTrace();
        }
    }
    
}
