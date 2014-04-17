package org.pieShare.pieTools.pieUtilities.service.commandParser.api;

import org.pieShare.pieTools.pieUtilities.service.commandParser.exception.CommandParserServiceException;

/**
 * Created by Svetoslav on 29.12.13.
 */
public interface ICommandParserService {
    public void parseArgs(String[] args) throws CommandParserServiceException;

    public void registerAction(IActionService action) throws CommandParserServiceException;
}
