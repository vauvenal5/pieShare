/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package commonTestTools.config;

import commonTestTools.overrides.ApplicationConfigurationTestService;
import loadTest.loadTestLib.LUtil;
import org.pieshare.piespring.service.ApplicationConfigurationService;
import org.pieShare.pieShareAppFx.springConfiguration.PieShareApp.PieShareAppService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

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
