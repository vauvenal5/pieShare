/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.pieshareserver;

import org.pieShare.pieShareApp.springConfiguration.PieUtilitiesConfiguration;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieshare.pieshareserver.services.ServerService;
import org.pieshare.pieshareserver.services.api.IServerService;
import org.pieshare.pieshareserver.springConfiguration.ServiceConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Richard
 */
public class App {

	private static AnnotationConfigApplicationContext context;

	public static void main(String[] args) {

	}

	public void start() {
		context = new AnnotationConfigApplicationContext();
		context.register(ServiceConfiguration.class);
		context.refresh();

		IBeanService beanService = context.getBean(BeanService.class);
		IServerService service = beanService.getBean(ServerService.class);
		service.startServer();
	}

}
