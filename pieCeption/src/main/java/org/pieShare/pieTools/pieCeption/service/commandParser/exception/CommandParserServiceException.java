package org.pieShare.pieTools.pieCeption.service.commandParser.exception;

/**
 * Created by Svetoslav on 29.12.13.
 */
public class CommandParserServiceException extends Exception {
    public CommandParserServiceException(String msg){
        super(msg);
    }

    public CommandParserServiceException(String msg, Throwable exception){
        super(msg, exception);
    }
}
