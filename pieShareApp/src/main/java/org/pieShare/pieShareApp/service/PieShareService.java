/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.pieShare.pieShareApp.api.IFileService;
import org.pieShare.pieShareApp.model.message.SimpleMessage;
import org.pieShare.pieShareApp.service.actionService.SimpleMessageActionService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.PrintEventTask;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.api.ICmdLineService;
import org.pieShare.pieTools.pieUtilities.service.commandParser.api.ICommandParserService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;

/**
 *
 * @author Svetoslav
 */
public class PieShareService
{

    private IExecutorService executorService;
    private ICommandParserService parserService;
    private ICmdLineService cmdLineService;
    private IBeanService beanService;
    private IClusterService clusterService;
    private IClusterManagementService clusterManagementService;
    private IFileService fileService;

    public PieShareService()
    {
    }

    public void setFileService(IFileService fileService)
    {
        this.fileService = fileService;
    }

    public void setExecutorService(IExecutorService service)
    {
        this.executorService = service;
    }

    public void setParserService(ICommandParserService service)
    {
        this.parserService = service;
    }

    public void setCommandLineService(ICmdLineService service)
    {
        this.cmdLineService = service;
    }

    public void setBeanService(IBeanService service)
    {
        this.beanService = service;
    }

    public void setClusterManagementService(IClusterManagementService service)
    {
        this.clusterManagementService = service;
    }

    @PostConstruct
    public void start()
    {
        /*try {
            this.clusterService = this.clusterManagementService.connect("ourFirstCluster");
        } catch (ClusterManagmentServiceException ex) {
            ex.printStackTrace();
        }*/

        this.executorService.registerExtendedTask(SimpleMessage.class, PrintEventTask.class);

        try
        {
            //todo-sv: change this!!! (new should not be used here)
            //getbean per class ist dumm... zerst?rt unabh?ngigkeit
            SimpleMessageActionService action = this.beanService.getBean(SimpleMessageActionService.class);
            this.parserService.registerAction(action);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        SimpleMessage msg = new SimpleMessage();
        msg.setMsg("PieShare awaits your command:");

        this.cmdLineService.writeLine(msg);
        this.fileService.sendAllFilesSyncRequest();
    }
}
