package org.pieShare.pieTools.piePlate.service.cluster.exception;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public class ClusterServiceException extends Exception {
    public ClusterServiceException(String msg){
        super(msg);
    }

    public ClusterServiceException(Exception e){
        super(e);
    }
}
