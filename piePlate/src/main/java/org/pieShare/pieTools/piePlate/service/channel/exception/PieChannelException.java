/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.channel.exception;

/**
 *
 * @author sveto_000
 */
public class PieChannelException extends Exception {

	public PieChannelException(String msg, Throwable ex) {
		super(msg, ex);
	}

	public PieChannelException(Throwable ex) {
		super(ex);
	}
}
