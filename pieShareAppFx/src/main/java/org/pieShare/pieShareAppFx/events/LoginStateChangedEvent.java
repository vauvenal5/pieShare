/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.events;

import java.util.EventObject;

/**
 *
 * @author Richard
 */
public class LoginStateChangedEvent extends EventObject {

	private boolean isLoggedIn = false;

	public LoginStateChangedEvent(Object source) {
		super(source);
	}

	public LoginStateChangedEvent(Object source, boolean isLoggedIn) {
		super(source);
		this.isLoggedIn = isLoggedIn;
	}

	public boolean isIsLoggedIn() {
		return isLoggedIn;
	}

	public void setIsLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

}
