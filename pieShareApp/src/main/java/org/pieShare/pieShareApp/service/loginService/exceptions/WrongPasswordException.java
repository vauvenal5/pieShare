/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.loginService.exceptions;

/**
 *
 * @author Richard
 */
public class WrongPasswordException extends Exception {

	public WrongPasswordException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public WrongPasswordException(String message) {
		super(message);
	}

}
