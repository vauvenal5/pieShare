/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.pieshareserver.springConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleConnectionMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHolePunchMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.RegisterMessage;
import org.pieShare.pieTools.piePlate.service.serializer.jacksonSerializer.JacksonSerializerService;
import org.pieShare.pieTools.piePlate.task.LoopHoleListenerTask;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.pieshare.pieshareserver.services.ServerService;
import org.pieshare.pieshareserver.services.loopHoleService.LoopHoleService;
import org.pieshare.pieshareserver.services.loopHoleService.UserPersistanceService;
import org.pieshare.pieshareserver.tasks.RegisterTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Richard
 */
public class ServiceConfiguration {

	@Bean
	@Lazy
	public ServerService serverService() {
		ServerService service = new ServerService();
		service.setBeanService(beanService());
		return service;
	}

	@Bean
	@Lazy
	public BeanService beanService() {
		return new BeanService();
	}

	@Bean
	@Lazy
	public ExecutorService javaExecutorService() {
		return java.util.concurrent.Executors.newCachedThreadPool();
	}

	@Bean
	@Lazy
	public PieExecutorService pieExecutorService() {
		PieExecutorService service = PieExecutorService.newCachedPieExecutorService();
		service.setExecutorFactory(this.pieExecutorTaskFactory());
		return service;
	}

	@Bean
	@Lazy
	public PieExecutorTaskFactory pieExecutorTaskFactory() {
		PieExecutorTaskFactory factory = new PieExecutorTaskFactory();
		factory.setBeanService(this.beanService());
		factory.setTasks(this.javaMap());
		return factory;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public Map javaMap() {
		return new HashMap();
	}

	@Bean
	@Lazy
	public JacksonSerializerService jacksonSerializerService() {
		return new JacksonSerializerService();
	}

	@Bean
	@Lazy
	public LoopHoleListenerTask loopHoleListenerTask() {
		LoopHoleListenerTask task = new LoopHoleListenerTask();
		task.setExcuterService(pieExecutorService());
		task.setSerializerService(jacksonSerializerService());
		return task;
	}

	@Bean
	@Lazy
	public LoopHoleService loopHoleService() {
		LoopHoleService service = new LoopHoleService();
		service.setBeanService(beanService());
		service.setExecutorService(pieExecutorService());
		service.setUserPersistanceService(userPersistanceService());
		service.setSerializerService(jacksonSerializerService());
		return service;
	}

	@Bean
	@Lazy
	public UserPersistanceService userPersistanceService() {
		UserPersistanceService service = new UserPersistanceService();
		return service;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public RegisterMessage registerMessage() {
		RegisterMessage message = new RegisterMessage();
		return message;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public LoopHoleConnectionMessage connectionMessage() {
		LoopHoleConnectionMessage msg = new LoopHoleConnectionMessage();
		return msg;
	}

	@Bean
	@Lazy
	@Scope(value = "prototype")
	public RegisterTask registerTask() {
		RegisterTask task = new RegisterTask();
		task.setUserPersistanceService(userPersistanceService());
		task.setLoopHoleService(loopHoleService());
		return task;
	}
}
