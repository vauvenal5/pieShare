/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieCeption.model.action;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieTools.pieCeption.service.api.IPieCeptionService;
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
    protected Map<String, Object> args;
    
    public void setPieCeptionService(IPieCeptionService service) {
        this.pieCeptionService = service;
    }
    
    public abstract ICommandMessage getCommandMessage();
    
    @Override
    public final void doAction(Map<String, Object> args) {
        this.args = args;
        this.pieCeptionService.handleCommand(this.getCommandMessage());
    }
}
