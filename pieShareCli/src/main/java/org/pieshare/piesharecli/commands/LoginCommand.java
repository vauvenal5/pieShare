/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.piesharecli.commands;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;


/**
 *
 * @author vauvenal5
 */
@Command(name = "login", description = "Login into your PieShare cloud.")
public class LoginCommand implements Runnable{
	
	@Arguments(description = "Your user name.")
	private String username;

	@Override
	public void run() {
		
		if(username == null) {
			System.out.println("Please enter our username:");
			username = "test";
		}
		
		System.out.println(username);
	}
}
