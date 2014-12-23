/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareClient;

import org.pieShare.pieShareClient.services.Client;
import org.pieShare.pieShareClient.springConfiguraton.PieShareClientServiceConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Richard
 */
public class App {

	private static AnnotationConfigApplicationContext context;

	public App() {

	}

	public static void main(String[] args) {
		context = new AnnotationConfigApplicationContext();
		context.register(PieShareClientServiceConfiguration.class);
		context.refresh();
		
                String from = args[0];
                String to = null;
                if(args.length == 2)
                 to = args[1];
		
		Client client  = new Client();
		client.connect(from, to);
		
	}
}
