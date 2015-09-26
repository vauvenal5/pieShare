/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.piesharecli.commands;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;


/**
 *
 * @author vauvenal5
 */
@Command(name = "login", description = "Login into your PieShare cloud.")
public class LoginUICommand implements Runnable{
	
	@Arguments(description = "Your user name.")
	private String username;

	@Override
	public void run() {
		
		Console console = System.console();
		
		if(username == null) {
			System.out.println("Please enter our username:");
			this.username = console.readLine();
		}
		
		System.out.println("Please enter our password:");
		PlainTextPassword pwd = new PlainTextPassword();
		pwd.password = (new String(console.readPassword())).getBytes(); 
		
		LoginCommand command = new LoginCommand();
		command.setPlainTextPassword(pwd);
		command.setUserName(username);
		
		//todo: send to pieShareApp
	}
}
