/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service;

import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.model.SimpleMessage;
import org.pieShare.pieShareApp.model.action.SimpleMessageAction;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.PrintEventTask;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.api.ICmdLineService;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.api.IPrintableEvent;
import org.pieShare.pieTools.pieUtilities.service.commandParser.api.ICommandParserService;
import org.pieShare.pieTools.pieUtilities.service.commandParser.exception.CommandParserServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;

/**
 *
 * @author Svetoslav
 */
public class PieShareService {
    private IExecutorService executorService;
    private ICommandParserService parserService;
    private ICmdLineService cmdLineService;
    
    public PieShareService() {
    }
    
    public void setExecutorService(IExecutorService service) {
        this.executorService = service;
    }
    
    public void setParserService(ICommandParserService service) {
        this.parserService = service;
    }
    
    public void setCommandLineService(ICmdLineService service) {
        this.cmdLineService = service;
    }
    
    @PostConstruct
    public void start() {
        this.executorService.registerExtendedTask(SimpleMessage.class, PrintEventTask.class);
        
        try {
            //todo-sv: change this!!! (new should not be used here)
            this.parserService.registerAction(new SimpleMessageAction());
        } catch (CommandParserServiceException ex) {
        }
        
        SimpleMessage msg = new SimpleMessage();
        msg.setMsg("PieShare awaits your command:");
        
        this.cmdLineService.writeLine(msg);
    }
}
