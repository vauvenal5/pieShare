/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database.api;

import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;
import org.pieShare.pieShareApp.model.PieShareConfiguration;
import org.pieShare.pieShareApp.model.entities.api.IConfigurationEntity;
import org.pieShare.pieShareApp.model.entities.api.IFileFilterEntity;
import org.pieShare.pieShareApp.model.entities.api.IPieFileEntity;
import org.pieShare.pieShareApp.model.entities.api.IPieUserEntity;
import org.pieShare.pieShareApp.service.fileFilterService.filters.RegexFileFilter;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;

/**
 *
 * @author Svetoslav
 */
public interface IModelEntityConverterService {

	IPieFileEntity convertToEntity(PieFile file);

	PieFile convertFromEntity(IPieFileEntity entity);

	IPieUserEntity convertToEntity(PieUser user);

	PieUser convertFromEntity(IPieUserEntity entity);

	PieShareConfiguration convertFromEntity(IConfigurationEntity entity);

	IConfigurationEntity convertToEntity(PieShareConfiguration conf, PieUser user);

	IFileFilterEntity convertToEntity(IFilter filter);

	RegexFileFilter convertFromEntity(IFileFilterEntity entity);

}
