/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.configurationService.api;

import org.pieShare.pieShareApp.model.entities.ConfigurationEntity;
import org.pieShare.pieShareApp.service.configurationService.PieShareConfiguration;

/**
 *
 * @author Richard
 */
public interface IConfigurationFactory {

	PieShareConfiguration createConfig();

	void checkAndCreateFolders(PieShareConfiguration conf);

	PieShareConfiguration confEntityToConf(ConfigurationEntity entity);

	ConfigurationEntity confToConfEntity(PieShareConfiguration conf);
}
