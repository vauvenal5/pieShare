/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.database.api;

import org.pieShare.pieShareApp.model.entities.PieFileEntity;
import org.pieShare.pieShareApp.model.pieFile.PieFile;

/**
 *
 * @author Svetoslav
 */
public interface IModelEntityConverterService {
	PieFileEntity convertToEntity(PieFile file);
	PieFile convertFromEntity(PieFileEntity entity);
}
