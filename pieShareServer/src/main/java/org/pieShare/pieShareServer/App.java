/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer;

import org.pieShare.pieTools.pieUtilities.service.beanService.BeanService;
import org.pieShare.pieShareServer.services.ServerService;
import org.pieShare.pieShareServer.services.api.IServerService;
import org.pieShare.pieShareServer.springConfiguration.ServiceConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Richard
 */
public class App {

	private static AnnotationConfigApplicationContext context;

	public static void main(String[] args) {
		context = new AnnotationConfigApplicationContext();
		context.register(ServiceConfiguration.class);
		context.refresh();
		
		BeanService beanService = context.getBean(BeanService.class);
		beanService.setApplicationContext(context);
		
		IServerService service = beanService.getBean(ServerService.class);
		service.startServer();
	}
}
