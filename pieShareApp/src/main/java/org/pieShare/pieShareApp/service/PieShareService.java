/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service;

import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.model.SimpleMessage;
import org.pieShare.pieShareApp.model.action.SimpleMessageAction;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.PrintEventTask;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
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
    private IBeanService beanService;
    private ICommandParserService parserService;
    
    public PieShareService() {
    }
    
    public void setExecutorService(IExecutorService service) {
        this.executorService = service;
    }
    
    @PostConstruct
    public void start() {
        this.executorService.registerExtendedTask(SimpleMessage.class, PrintEventTask.class);
        
        try {
            this.parserService.registerAction(new SimpleMessageAction());
        } catch (CommandParserServiceException ex) {
        }
    }
}
