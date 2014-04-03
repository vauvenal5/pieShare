/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.model.SimpleMessage;
import org.pieShare.pieShareApp.model.action.SimpleMessageAction;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.PrintEventTask;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.api.ICmdLineService;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.api.IPrintableEvent;
import org.pieShare.pieTools.pieUtilities.service.commandParser.api.ICommandParserService;
import org.pieShare.pieTools.pieUtilities.service.commandParser.exception.CommandParserServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Svetoslav
 */
public class PieShareService {
    private IExecutorService executorService;
    private ICommandParserService parserService;
    private ICmdLineService cmdLineService;
    private IBeanService beanService;
    private IClusterService clusterService;
    
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
    
    public void setBeanService(IBeanService service) {
        this.beanService = service;
    }
    
    public void setClusterService(IClusterService service) {
        this.clusterService = service;
    }
    
    @PostConstruct
    public void start() {
        
        try {
            this.clusterService.connect("ourFirstCluster");
        } catch (ClusterServiceException ex) {
            ex.printStackTrace();
        }
        
        this.executorService.registerExtendedTask(SimpleMessage.class, PrintEventTask.class);
        
        try {
            //todo-sv: change this!!! (new should not be used here)
            SimpleMessageAction action = this.beanService.getBean(SimpleMessageAction.class);
            this.parserService.registerAction(action);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        SimpleMessage msg = new SimpleMessage();
        msg.setMsg("PieShare awaits your command:");
        
        this.cmdLineService.writeLine(msg);
    }
}
