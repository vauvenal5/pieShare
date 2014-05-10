/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.piePlate.service.cluster.exception;

/**
 *
 * @author Svetoslav
 */
public class ClusterManagmentServiceException extends Exception {

    /**
     * Creates a new instance of <code>ClusterManagmentServiceException</code>
     * without detail message.
     */
    public ClusterManagmentServiceException() {
    }

    /**
     * Constructs an instance of <code>ClusterManagmentServiceException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ClusterManagmentServiceException(String msg) {
        super(msg);
    }
    
    public ClusterManagmentServiceException(Throwable ex) {
        super(ex);
    }
}
