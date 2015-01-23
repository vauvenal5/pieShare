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
public class NoLocalFileException extends Exception {

	/**
	 * Creates a new instance of <code>NoLocalFileException</code> without
	 * detail message.
	 */
	public NoLocalFileException() {
	}

	/**
	 * Constructs an instance of <code>NoLocalFileException</code> with the
	 * specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public NoLocalFileException(String msg) {
		super(msg);
	}
}
