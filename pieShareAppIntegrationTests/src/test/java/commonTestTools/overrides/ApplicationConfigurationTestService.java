/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTestTools.overrides;

import java.io.File;
import org.pieshare.piespring.service.ApplicationConfigurationService;

/**
 *
 * @author vauvenal5
 */
public class ApplicationConfigurationTestService extends ApplicationConfigurationService {
        public void setConfigPath(String folder) {
		BASE_CONFIG_FOLDER = new File(folder);
		if (!BASE_CONFIG_FOLDER.exists()) {
			BASE_CONFIG_FOLDER.mkdirs();
		}
	}
}
