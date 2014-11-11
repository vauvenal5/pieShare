/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database.api;

import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.api.IBaseModel;
import org.pieShare.pieShareApp.model.entities.ConfigurationEntity;
import org.pieShare.pieShareApp.model.entities.FilterEntity;
import org.pieShare.pieShareApp.model.entities.PieFileEntity;
import org.pieShare.pieShareApp.model.entities.PieUserEntity;
import org.pieShare.pieShareApp.model.entities.api.IBaseEntity;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.configurationService.PieShareConfiguration;
import org.pieShare.pieShareApp.service.database.exception.NotConvertableException;
import org.pieShare.pieShareApp.service.fileFilterService.filters.RegexFileFilter;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;

/**
 *
 * @author Svetoslav
 */
public interface IModelEntityConverterService {

	<T extends IBaseModel> IBaseEntity convertToEntity(T model) throws NotConvertableException;

	IBaseModel convertFromEntity(IBaseEntity entity) throws NotConvertableException;
	
	PieFileEntity convertToEntity(PieFile file) throws NotConvertableException;

	PieFile convertFromEntity(PieFileEntity entity) throws NotConvertableException;

	PieUserEntity convertToEntity(PieUser user) throws NotConvertableException;

	PieUser convertFromEntity(PieUserEntity entity) throws NotConvertableException;

	PieShareConfiguration convertFromEntity(ConfigurationEntity entity) throws NotConvertableException;

	ConfigurationEntity convertToEntity(PieShareConfiguration conf) throws NotConvertableException;

	FilterEntity convertToEntity(IFilter filter) throws NotConvertableException;

	RegexFileFilter convertFromEntity(FilterEntity entity) throws NotConvertableException;

}
