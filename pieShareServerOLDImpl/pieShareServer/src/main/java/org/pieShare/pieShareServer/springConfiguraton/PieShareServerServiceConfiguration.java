/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.springConfiguraton;

import org.pieShare.pieShareServer.services.InputTask;
import org.pieShare.pieShareServer.services.Server;
import org.pieShare.pieShareServer.services.UserPersistenceService;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Richard
 */
@Configuration
public class PieShareServerServiceConfiguration {

	@Bean
	@Lazy
	public Server server() {
		Server server = new Server();
		server.setBeanService(beanService());
		server.setInputTask(inputTask());
		return server;
	}

	@Bean
	@Lazy
	public BeanService beanService() {
		BeanService beanService = new BeanService();
		return beanService;
	}

	@Bean
	@Lazy
	public UserPersistenceService userPersistenceService() {
		UserPersistenceService service = new UserPersistenceService();
		return service;
	}
        
        
	@Bean
	@Lazy
	@Scope(value = "prototype")
	public InputTask inputTask() {
		InputTask incomeTask = new InputTask();
		incomeTask.setUserPersistenceService(userPersistenceService());
		return incomeTask;
	}
}
