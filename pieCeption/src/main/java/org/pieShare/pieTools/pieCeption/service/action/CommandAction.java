/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieCeption.service.action;

import org.pieShare.pieTools.pieCeption.service.core.api.IPieCeptionService;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.pieUtilities.service.commandParser.api.IAction;

/**
 *
 * @author Svetoslav
 */
public abstract class CommandAction implements IAction {

    private IPieCeptionService pieCeptionService;
    
    public void setPieCeptionService(IPieCeptionService service) {
        this.pieCeptionService = service;
    }
    
    protected void commitPieMessage(IPieMessage message) {
        this.pieCeptionService.handlePieMessage(message);
    }
    
}
