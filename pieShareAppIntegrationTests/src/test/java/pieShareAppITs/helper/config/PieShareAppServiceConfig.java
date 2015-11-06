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
import org.mockito.Mockito;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.service.configurationService.ApplicationConfigurationService;
import org.pieShare.pieShareApp.service.database.DatabaseService;
import org.pieShare.pieShareAppFx.springConfiguration.PieShareApp.PieShareAppService;
import org.pieShare.pieShareAppFx.springConfiguration.PieShareApp.PieShareAppTasks;
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
	
	@Bean
	@Lazy
	public ITTasksCounter itTasksCounter() {
		return new ITTasksCounter();
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
