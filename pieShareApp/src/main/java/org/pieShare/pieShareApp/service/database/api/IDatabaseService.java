/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database.api;

import java.util.ArrayList;
import javax.persistence.EntityManager;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.entities.BaseEntity;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;

/**
 *
 * @author Richard
 */
public interface IDatabaseService {

	void persistPieUser(PieUser service);

	void mergePieUser(PieUser service);

	PieUser getPieUser(String name);

	PieUser findPieUser();

	void removePieUser(PieUser user);

	void persistFileFilter(IFilter filter);

	void removeFileFilter(IFilter filter);

	ArrayList<IFilter> findAllFilters();
	
	<T extends BaseEntity> T findEntity(EntityManager em, Class<T> clazz, Object key);
}
