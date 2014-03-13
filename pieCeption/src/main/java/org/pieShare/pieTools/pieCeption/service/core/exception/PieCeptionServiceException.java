package org.pieShare.pieTools.pieCeption.service.core.exception;

/**
 * Created by Svetoslav on 29.12.13.
 */
public class PieCeptionServiceException extends Exception {
    public PieCeptionServiceException(String msg){
        super(msg);
    }

    public PieCeptionServiceException(String msg, Throwable exception){
        super(msg, exception);
    }

    public PieCeptionServiceException(Throwable exception){
        super(exception);
    }
}
