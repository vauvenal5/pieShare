/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.loginService.event;

import java.util.EventObject;
import org.pieShare.pieShareApp.service.loginService.event.enumeration.LoginState;

/**
 *
 * @author Richard
 */
public class LoginFinished extends EventObject {

	private LoginState state;
	private Exception exception;
	
	public LoginFinished(Object source, LoginState state) {
		super(source);
		this.state = state;
	}

	public LoginFinished(Object source, LoginState state, Exception exception) {
		super(source);
		this.state = state;
		this.exception = exception;
	}

	public LoginState getState() {
		return state;
	}

	public void setState(LoginState state) {
		this.state = state;
	}

}
