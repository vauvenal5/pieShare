package org.pieTools.pieCeption.service.core;

import org.pieTools.pieCeption.service.commandParser.api.ICommandParserService;
import org.pieTools.pieCeption.service.commandParser.exception.CommandParserServiceException;
import org.pieTools.pieCeption.service.core.api.IPieCeptionConnectorService;
import org.pieTools.pieCeption.service.core.api.IPieCeptionService;
import org.pieTools.pieCeption.service.core.api.IStartupService;
import org.pieTools.pieCeption.service.core.exception.PieCeptionServiceException;
import org.pieTools.pieCeption.service.core.exception.StartupServiceException;

/**
 * Created by Svetoslav on 29.12.13.
 */
public class PieCeptionService implements IPieCeptionService {
    private IPieCeptionConnectorService connectorService;
    private ICommandParserService commandParserService;
    private IStartupService startupService;

    public PieCeptionService(){
    }

    public void setConnectorService(IPieCeptionConnectorService connectorService) {
        this.connectorService = connectorService;
    }

    public void setCommandParserService(ICommandParserService commandParserService){
        this.commandParserService = commandParserService;
    }

    @Override
    public void parseArgs(String[] args) throws PieCeptionServiceException {

        this.connectorService.connectToMaster();

        if(!this.connectorService.isPieShareRunning()) {
            try {
                this.startupService.startInstance();
                //todo-sv: how to handle the startup time of master?
            } catch (StartupServiceException e1) {
                throw new PieCeptionServiceException("Starting master failed!", e1);
            }
        }

        try {
            this.commandParserService.parseArgs(args);
        } catch (CommandParserServiceException e) {
            throw new PieCeptionServiceException(e);
        }
    }
}
