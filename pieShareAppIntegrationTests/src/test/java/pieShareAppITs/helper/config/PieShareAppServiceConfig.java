/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pieShareAppITs.helper.config;

import pieShareAppITs.helper.ITTasksCounter;
import org.pieShare.pieShareAppFx.springConfiguration.PieShareApp.PieShareAppService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.PieExecutorTaskFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

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
