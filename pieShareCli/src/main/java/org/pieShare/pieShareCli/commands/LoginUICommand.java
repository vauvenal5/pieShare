/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareCli.commands;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import java.io.Console;
import org.pieShare.pieShareApp.model.command.LoginCommand;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


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
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity request = new HttpEntity(command);
		ResponseEntity<String> resp = restTemplate.exchange("http://127.0.0.1:8080/login", 
				HttpMethod.POST, request, new ParameterizedTypeReference<String>(){});
		
		System.out.println(resp.getBody());
	}
}
