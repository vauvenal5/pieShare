/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database.exception;

/**
 *
 * @author Richard
 */
public class NotConvertableException extends Exception {

	public NotConvertableException(String message) {
		super(message);
	}

	public NotConvertableException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
