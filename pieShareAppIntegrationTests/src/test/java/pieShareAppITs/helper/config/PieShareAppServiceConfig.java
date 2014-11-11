/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pieShareAppITs.helper.config;

import pieShareAppITs.helper.ITTasksCounter;
import pieShareAppITs.helper.tasks.TestTask;
import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mockito.Mockito;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.service.configurationService.ApplicationConfigurationService;
import org.pieShare.pieShareApp.service.database.DatabaseService;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppService;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppTasks;
import org.pieShare.pieTools.pieUtilities.service.propertiesReader.PropertiesReader;
import org.pieShare.pieTools.pieUtilities.service.propertiesReader.exception.NoConfigFoundException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import pieShareAppITs.helper.ITUtil;

/**
 *
 * @author Svetoslav
 */
@Configuration
public class PieShareAppServiceConfig extends PieShareAppService {	
	@Autowired
	protected PieShareAppTasks tasks;
	
	@Bean
	@Lazy
	@Override
	public DatabaseService databaseService() {
		DatabaseService service = Mockito.mock(DatabaseService.class);
		Mockito.when(service.findAllPieUser()).thenReturn(null);
		return service;
	}
	
	@Bean
	@Lazy
	public ITTasksCounter itTasksCounter() {
		return new ITTasksCounter();
	}
	
	/*@Bean
	@Lazy
	public FileTranserferCompleteTestTask fileTransferCompleteTestTask() {
		FileTranserferCompleteTestTask task = new FileTranserferCompleteTestTask();
		task.setShareService(this.shareService());
		task.setUtil(this.itTasksCounter());
		return task;
	}*/
	
	@Bean
	@Lazy
	public TestTask testTask() {
		TestTask task = new TestTask();
		task.setFactory(this.testTaskFactory());
		task.setUtil(this.itTasksCounter());
		return task;
	}
	
	@Bean
	@Lazy
	public PieExecutorTaskFactory testTaskFactory() {
		PieExecutorTaskFactory factory = new PieExecutorTaskFactory();
		factory.setBeanService(this.utilities.beanService());
		factory.setTasks(this.utilities.javaMap());
		return factory;
	}
}
