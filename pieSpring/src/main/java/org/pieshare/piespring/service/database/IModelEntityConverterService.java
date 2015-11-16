/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.piespring.service.database;

import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.entities.ConfigurationEntity;
import org.pieShare.pieShareApp.model.entities.FilterEntity;
import org.pieShare.pieShareApp.model.entities.PieFileEntity;
import org.pieShare.pieShareApp.model.entities.PieUserEntity;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.service.fileFilterService.filters.RegexFileFilter;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;

/**
 *
 * @author Svetoslav
 */
public interface IModelEntityConverterService {

	PieFileEntity convertToEntity(PieFile file);

	PieFile convertFromEntity(PieFileEntity entity);

	PieUserEntity convertToEntity(PieUser user);

	PieUser convertFromEntity(PieUserEntity entity);

	PieShareConfiguration convertFromEntity(ConfigurationEntity entity);

	ConfigurationEntity convertToEntity(PieShareConfiguration conf, PieUser user);

	FilterEntity convertToEntity(IFilter filter);

	RegexFileFilter convertFromEntity(FilterEntity entity);

}
