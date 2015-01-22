/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer;

import org.pieShare.pieShareServer.services.Server;
import org.pieShare.pieShareServer.services.api.IServer;
import org.pieShare.pieShareServer.springConfiguraton.PieShareServerServiceConfiguration;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanService;
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
		context.register(PieShareServerServiceConfiguration.class);
		context.refresh();
		BeanService beanService = context.getBean(BeanService.class);
		beanService.setApplicationContext(context);

		IServer server = context.getBean(Server.class);
		server.start();
	}
}
