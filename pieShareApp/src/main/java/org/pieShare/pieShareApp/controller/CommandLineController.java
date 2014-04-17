/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.controller;

import org.pieShare.pieShareApp.controller.api.ISimpleMessageController;
import org.pieShare.pieShareApp.model.SimpleMessageCommand;
import org.pieShare.pieShareApp.service.commandService.ICommandService;

/**
 *
 * @author Svetoslav
 */
public class CommandLineController implements ISimpleMessageController {
    
    private ICommandService<SimpleMessageCommand> simpleMessageService;
    
    public void setSimpleMessageService(ICommandService<SimpleMessageCommand> service) {
        this.simpleMessageService = service;
    }

    @Override
    public void handleCommand(SimpleMessageCommand command) {
        this.simpleMessageService.executeCommand(command);
    }
    
}
