package org.pieTools.pieCeption.service.core;

import org.pieTools.pieCeption.service.commandParser.api.ICommandParserService;
import org.pieTools.pieCeption.service.commandParser.exception.CommandParserServiceException;
import org.pieTools.pieCeption.service.core.api.IPieCeptionConnectorService;
import org.pieTools.pieCeption.service.core.api.IPieCeptionService;
import org.pieTools.pieCeption.service.core.exception.PieCeptionServiceException;

/**
 * Created by Svetoslav on 29.12.13.
 */
public class PieCeptionService implements IPieCeptionService {
    private IPieCeptionConnectorService connectorService;
    private ICommandParserService commandParserService;

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
        try {
            this.commandParserService.parseArgs(args);
        } catch (CommandParserServiceException e) {
            throw new PieCeptionServiceException(e);
        }
    }
}
