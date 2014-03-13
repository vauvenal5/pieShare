package org.pieShare.pieTools.piePlate.controller.exception;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public class ClusterException extends Exception {

    public ClusterException(String msg, Throwable e)
    {
        super(msg, e);
    }


    public ClusterException(String msg){
        super(msg);
    }

    public ClusterException(Exception e){
        super(e);
    }
}
