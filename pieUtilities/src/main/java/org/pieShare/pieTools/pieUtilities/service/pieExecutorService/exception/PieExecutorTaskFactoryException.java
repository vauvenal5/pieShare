/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception;

/**
 *
 * @author Svetoslav
 */
public class PieExecutorTaskFactoryException extends Exception {

	/**
	 * Creates a new instance of <code>PieExecutorServiceException</code>
	 * without detail message.
	 */
	public PieExecutorTaskFactoryException(String msg, Throwable ex) {
		super(msg, ex);
	}

	/**
	 * Constructs an instance of <code>PieExecutorServiceException</code> with
	 * the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public PieExecutorTaskFactoryException(String msg) {
		super(msg);
	}
}
