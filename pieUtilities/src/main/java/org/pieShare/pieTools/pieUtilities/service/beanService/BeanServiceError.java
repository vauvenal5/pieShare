/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.beanService;

/**
 *
 * @author Svetoslav
 */
public class BeanServiceError extends Error {

    /**
     * Creates a new instance of <code>BeanServiceException</code> without
     * detail message.
     */
    public BeanServiceError(Throwable ex) {
        super(ex);
    }

    /**
     * Constructs an instance of <code>BeanServiceException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public BeanServiceError(String msg) {
        super(msg);
    }
}
