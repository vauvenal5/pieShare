/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.database;

import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.entities.PieFileEntity;
import org.pieShare.pieShareApp.model.entities.PieUserEntity;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.database.api.IModelEntityConverterService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 *
 * @author Svetoslav
 */
public class ModelEntityConverterService implements IModelEntityConverterService {
	
	private IBeanService beanService;

	@Override
	public PieFileEntity convertToEntity(PieFile file) {
		PieFileEntity entity = this.beanService.getBean(PieFileEntity.class);
		entity.setMd5(file.getMd5());
		entity.setLastModified(file.getLastModified());
		entity.setFileName(file.getFileName());
		entity.setRelativeFilePath(file.getRelativeFilePath());
		return entity;
	}
	
	public PieUserEntity convertToEntity(PieUser user) {
		PieUserEntity entity = this.beanService.getBean(PieUserEntity.class);
		//todo: fill this
		return entity;
	}

	@Override
	public PieFile convertFromEntity(PieFileEntity entity) {
		PieFile file = this.beanService.getBean(PieFile.class);
		file.setFileName(entity.getFileName());
		file.setLastModified(entity.getLastModified());
		file.setMd5(entity.getMd5());
		file.setRelativeFilePath(entity.getRelativeFilePath());
		return file;
	}
	
}
