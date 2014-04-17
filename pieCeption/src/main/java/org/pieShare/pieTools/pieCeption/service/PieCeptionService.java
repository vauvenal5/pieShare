package org.pieShare.pieTools.pieCeption.service;

import org.pieShare.pieTools.pieCeption.model.action.ICommandMessage;
import org.pieShare.pieTools.pieCeption.service.api.IConnectorService;
import org.pieShare.pieTools.pieCeption.service.api.IPieCeptionService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;

/**
 * Created by Svetoslav on 29.12.13.
 */
public class PieCeptionService implements IPieCeptionService {
    private IConnectorService connectorService;
    private IExecutorService executorService;

    public PieCeptionService(){
    }

    public void setConnectorService(IConnectorService connectorService) {
        this.connectorService = connectorService;
    }
    
    public void setExecutorService(IExecutorService service) {
        this.executorService = service;
    }

    @Override
    public void handleCommand(ICommandMessage command) {
        if(!this.connectorService.isPieShareRunning()) {
            /*try {
                this.executorService.handlePieEvent(command.getMessage());
            } catch(PieExecutorServiceException ex) {
                throw new ShouldNeverHappenError("This can only happen by a developer mistake!", ex);
            }*/
            
            command.executeCommand();
            return;
        }

        this.connectorService.sendToMaster(command);
    }
}
