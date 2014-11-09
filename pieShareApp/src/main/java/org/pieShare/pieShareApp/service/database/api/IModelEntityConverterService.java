/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database.api;

import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.entities.ConfigurationEntity;
import org.pieShare.pieShareApp.model.entities.PieFileEntity;
import org.pieShare.pieShareApp.model.entities.PieUserEntity;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.configurationService.PieShareConfiguration;

/**
 *
 * @author Svetoslav
 */
public interface IModelEntityConverterService {

	PieFileEntity convertToEntity(PieFile file);

	PieFile convertFromEntity(PieFileEntity entity);

	PieShareConfiguration confEntityToConf(ConfigurationEntity entity);

	ConfigurationEntity confToConfEntity(PieShareConfiguration conf);

	PieUserEntity userToEntity(PieUser user);

	PieUser entityToUser(PieUserEntity entity);
}
