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
	
	public static boolean main;
	@Autowired
	protected PieShareAppTasks tasks;
	
	@Bean
	@Lazy
	@Override
	public DatabaseService databaseService() {
		DatabaseService service = Mockito.mock(DatabaseService.class);
		Mockito.when(service.getPieUser("test")).thenReturn(null);
		Mockito.when(service.findPieUser()).thenReturn(null);
		return service;
	}
	
//	@Bean
//	@Lazy
//	@Override
//	public PieUser applicationConfigurationService() {
//		if(main){
//			return this.pieShareAppMainConfiguration();
//		}
//		
//		return this.pieShareAppOtherConfiguration();
//	}
//	
//	@Bean
//	@Lazy
//	public PieShareAppConfiguration pieShareAppMainConfiguration() {
//		Properties properties = new Properties();
//		properties.put("tempCopyDir", ITUtil.getMainTmpDir());
//		properties.put("workingDir", ITUtil.getMainWorkingDir());
//		PropertiesReader reader = Mockito.mock(PropertiesReader.class);
//		try {
//			Mockito.when(reader.getConfig(Mockito.any())).thenReturn(properties);
//			//Mockito.when(reader.getBaseConfigPath()).thenReturn(new File(""));
//		} catch (NoConfigFoundException ex) {
//			Logger.getLogger(PieShareAppServiceConfig.class.getName()).log(Level.SEVERE, null, ex);
//		}
//		
//		PieShareAppConfiguration config = new PieShareAppConfiguration();
//		config.setConfigurationReader(reader);
//		config.setConfigPath("test");
//		config.init();
//		return config;
//	}
//	
//	@Bean
//	@Lazy
//	public PieShareAppConfiguration pieShareAppOtherConfiguration() {
//		Properties properties = new Properties();
//		properties.put("tempCopyDir", ITUtil.getBotTmpDir());
//		properties.put("workingDir", ITUtil.getBotWorkingDir());
//		PropertiesReader reader = Mockito.mock(PropertiesReader.class);
//		try {
//			Mockito.when(reader.getConfig(Mockito.any())).thenReturn(properties);
//			//Mockito.when(reader.getBaseConfigPath()).thenReturn(new File(""));
//		} catch (NoConfigFoundException ex) {
//			Logger.getLogger(PieShareAppServiceConfig.class.getName()).log(Level.SEVERE, null, ex);
//		}
//		
//		PieShareAppConfiguration config = new PieShareAppConfiguration();
//		config.setConfigurationReader(reader);
//		config.setConfigPath("test");
//		config.init();
//		return config;
//	}
	
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
