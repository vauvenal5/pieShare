/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pieShareAppITs.helper.config;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mockito.Mockito;
import org.pieShare.pieShareApp.springConfiguration.PieUtilitiesConfiguration;
import org.pieShare.pieTools.pieUtilities.service.propertiesReader.PropertiesReader;
import org.pieShare.pieTools.pieUtilities.service.propertiesReader.exception.NoConfigFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import static pieShareAppITs.helper.config.PieShareAppServiceConfig.main;

/**
 *
 * @author Svetoslav
 */
@Configuration
public class PieShareUtilitiesITConfig extends PieUtilitiesConfiguration {
	@Bean
	@Lazy
	@Override
	public PropertiesReader configurationReader() {
		Properties prop = new Properties();
		
		if(PieShareAppServiceConfig.main) {
			prop.put("databaseDir", "mainDb");
		}
		else {
			prop.put("databaseDir", "botDb");
		}
		
		PropertiesReader reader = Mockito.mock(PropertiesReader.class);
		File file = Mockito.any(File.class);
		
		try {
			Mockito.when(reader.getConfig(file)).thenReturn(prop);
		} catch (NoConfigFoundException ex) {
		}
		
		return reader;
	}
}
