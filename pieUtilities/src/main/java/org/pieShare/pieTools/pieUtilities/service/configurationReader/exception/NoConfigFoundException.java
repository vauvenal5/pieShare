/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.configurationReader.exception;

/**
 *
 * @author richy
 */
public class NoConfigFoundException extends Exception {
	
	
	public NoConfigFoundException(String message) {
		super(message);
	}
	
	public NoConfigFoundException(Throwable th) {
		super(th);
	}

}
