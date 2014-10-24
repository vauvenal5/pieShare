/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package integrationTests;

import org.mockito.Mockito;
import org.pieShare.pieShareApp.service.configurationService.PieShareAppConfiguration;
import org.pieShare.pieShareApp.service.database.DatabaseService;
import org.pieShare.pieShareApp.springConfiguration.PieShareApp.PieShareAppService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 *
 * @author Svetoslav
 */
@Configuration
public class PieShareAppServiceConfig extends PieShareAppService {
	
	public static String configFile = "pieShareTest.properties";
	
	@Bean
	@Lazy
	@Override
	public DatabaseService databaseService() {
		DatabaseService service = Mockito.mock(DatabaseService.class);
		Mockito.when(service.getPieUser("test")).thenReturn(null);
		return service;
	}
	
	
	@Bean
	@Lazy
	@Override
	public PieShareAppConfiguration pieShareAppConfiguration() {
		PieShareAppConfiguration service = new PieShareAppConfiguration();
		service.setConfigurationReader(this.utilities.configurationReader());
		service.setConfigPath(configFile);
		service.init();
		return service;
	}
	
	@Bean
	@Lazy
	public IntegrationTestUtil integrationTestUtil() {
		return new IntegrationTestUtil();
	}
	
	@Bean
	@Lazy
	public FileTranserferCompleteTestTask fileTransferCompleteTestTask() {
		FileTranserferCompleteTestTask task = new FileTranserferCompleteTestTask();
		task.setShareService(this.shareService());
		task.setUtil(this.integrationTestUtil());
		return task;
	}
}
