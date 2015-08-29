/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package commonTestTools.config;

import commonTestTools.overrides.ApplicationConfigurationTestService;
import pieShareAppITs.helper.config.*;
import pieShareAppITs.helper.ITTasksCounter;
import pieShareAppITs.helper.tasks.TestTask;
import java.io.File;
import java.util.Properties;
import loadTest.loadTestLib.LUtil;
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
public class PieShareAppServiceTestConfig extends PieShareAppService {	
	@Bean
	@Lazy
        @Override
	public ApplicationConfigurationService applicationConfigurationService() {
		ApplicationConfigurationTestService service = new ApplicationConfigurationTestService();
		service.setPropertiesReader(utilities.configurationReader());
		service.setBeanService(utilities.beanService());
                service.setConfigPath(LUtil.getConfigDir());
		service.init();
		return service;
	}
}
