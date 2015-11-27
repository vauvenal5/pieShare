/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.discovery;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class DiscoveryException extends Exception {

	/**
	 * Creates a new instance of <code>DiscoveryException</code> without detail
	 * message.
	 */
	public DiscoveryException(String msg, Throwable ex) {
		super(msg, ex);
	}

	/**
	 * Constructs an instance of <code>DiscoveryException</code> with the
	 * specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public DiscoveryException(String msg) {
		super(msg);
	}
}
