package org.pieTools.pieCeption.service.commandParser.api;

import org.pieTools.pieCeption.service.commandParser.exception.CommandParserServiceException;

/**
 * Created by Svetoslav on 29.12.13.
 */
public interface ICommandParserService {
    public void parseArgs(String[] args) throws CommandParserServiceException;

    public void registerAction(IAction action) throws CommandParserServiceException;
}
