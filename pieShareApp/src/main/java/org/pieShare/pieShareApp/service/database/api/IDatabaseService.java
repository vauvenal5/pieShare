/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database.api;

import java.util.ArrayList;
import javax.persistence.EntityManager;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.api.IBaseModel;
import org.pieShare.pieShareApp.model.entities.BaseEntity;
import org.pieShare.pieShareApp.model.entities.api.IBaseEntity;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;

/**
 *
 * @author Richard
 */
public interface IDatabaseService {

	void persist(PieUser model);

	public ArrayList<PieUser> findAllPieUser();

	void removePieUser(PieUser user);

	void mergePieUser(PieUser user);

	void persistFileFilter(IFilter filter);

//	void persist(PieFile file);
	void removeFileFilter(IFilter filter);

	ArrayList<IFilter> findAllFilters();

	<T extends IBaseEntity> T findEntity(Class<T> clazz, Object key);
}
