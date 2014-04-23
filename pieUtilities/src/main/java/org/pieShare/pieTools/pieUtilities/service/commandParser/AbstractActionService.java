/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.commandParser;

import java.util.Map;
import org.pieShare.pieTools.pieUtilities.model.command.ICommand;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanService;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanServiceUser;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.commandParser.api.IActionService;
import org.pieShare.pieTools.pieUtilities.service.commandService.api.ICommandService;

/**
 *
 * @author Svetoslav
 */
public abstract class AbstractActionService<C extends ICommand, T extends ICommandService<C>> extends BeanServiceUser implements IActionService {
    protected T commandService;
    
    public void setCommandService(T service) {
        this.commandService = service;
    }
    
    public abstract C getCommand(Map<String, Object> args);
    
    @Override
    public String getProgramName() {
        return "pieShare";
    }
    
    @Override
    public final void doAction(Map<String, Object> args) {
        this.commandService.executeCommand(this.getCommand(args));
    }
}
