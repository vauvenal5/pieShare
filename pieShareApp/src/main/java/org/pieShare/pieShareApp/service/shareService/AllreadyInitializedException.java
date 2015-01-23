/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.shareService;

/**
 *
 * @author Svetoslav
 */
public class AllreadyInitializedException extends Exception {

	/**
	 * Creates a new instance of <code>AllreadyInitializedException</code>
	 * without detail message.
	 */
	public AllreadyInitializedException() {
	}

	/**
	 * Constructs an instance of <code>AllreadyInitializedException</code> with
	 * the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public AllreadyInitializedException(String msg) {
		super(msg);
	}
}
