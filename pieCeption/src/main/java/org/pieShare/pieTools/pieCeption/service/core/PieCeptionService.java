package org.pieShare.pieTools.pieCeption.service.core;

import javax.annotation.PostConstruct;
import org.pieShare.pieTools.pieCeption.service.action.ICommand;
import org.pieShare.pieTools.pieCeption.service.core.api.IConnectorService;
import org.pieShare.pieTools.pieCeption.service.core.api.IPieCeptionService;
import org.pieShare.pieTools.pieCeption.service.core.exception.PieCeptionServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorServiceException;

/**
 * Created by Svetoslav on 29.12.13.
 */
public class PieCeptionService implements IPieCeptionService {
    private IConnectorService connectorService;
    private IExecutorService executorService;
    private boolean isMaster;

    public PieCeptionService(){
    }

    public void setConnectorService(IConnectorService connectorService) {
        this.connectorService = connectorService;
    }
    
    public void setExecutorService(IExecutorService service) {
        this.executorService = service;
    }

    @PostConstruct
    public void init() throws PieCeptionServiceException {
        this.isMaster = !this.connectorService.isPieShareRunning();
    }

    @Override
    public void handleCommand(ICommand command) {
        if(this.isMaster) {
            try {
                this.executorService.handlePieEvent(command.getMessage());
            } catch(PieExecutorServiceException ex) {
                //todo-sv: command doesn't exist therefore print usage!
            }
            return;
        }

        this.connectorService.sendToMaster(command);
    }
}
