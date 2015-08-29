/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.shareService;

/**
 *
 * @author vauvenal5
 */
public class CouldNotCreateMetaDataException extends Exception {

	/**
	 * Creates a new instance of <code>CouldNotCreateMetaDataException</code>
	 * without detail message.
	 */
	public CouldNotCreateMetaDataException() {
	}

	/**
	 * Constructs an instance of <code>CouldNotCreateMetaDataException</code>
	 * with the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public CouldNotCreateMetaDataException(String msg) {
		super(msg);
	}
	
	public CouldNotCreateMetaDataException(Throwable t) {
		super(t);
	}
}
