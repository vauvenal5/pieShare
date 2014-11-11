/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.entities.FilterEntity;
import org.pieShare.pieShareApp.model.entities.PieFileEntity;
import org.pieShare.pieShareApp.model.entities.PieUserEntity;
import org.pieShare.pieShareApp.model.entities.api.IBaseEntity;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.configurationService.api.IConfigurationFactory;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.service.database.api.IModelEntityConverterService;
import org.pieShare.pieShareApp.service.database.api.IPieDatabaseManagerFactory;
import org.pieShare.pieShareApp.service.fileFilterService.filters.RegexFileFilter;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

public class DatabaseService implements IDatabaseService {

	private IPieDatabaseManagerFactory pieDatabaseManagerFactory;
	private EntityManagerFactory emf;
	private IBase64Service base64Service;
	private IBeanService beanService;
	private IConfigurationFactory configurationFactory;
	private IModelEntityConverterService modelEntityConverterService;

	public void setModelEntityConverterService(IModelEntityConverterService modelEntityConverterService) {
		this.modelEntityConverterService = modelEntityConverterService;
	}

	public void setConfigurationFactory(IConfigurationFactory configurationFactory) {
		this.configurationFactory = configurationFactory;
	}

	public void setPieDatabaseManagerFactory(IPieDatabaseManagerFactory factory) {
		this.pieDatabaseManagerFactory = factory;
	}

	public void setBase64Service(IBase64Service base64Service) {
		this.base64Service = base64Service;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	@Override
	public void persist(PieUser model) {
		PieUserEntity entity;
		entity = this.modelEntityConverterService.convertToEntity(model);
		persist(entity);
	}

	@Override
	public ArrayList<PieUser> findAllPieUser() {
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(PieUserEntity.class);

		Query query = em.createQuery(String.format("SELECT e FROM %s e", PieUserEntity.class.getSimpleName()), PieUserEntity.class);
		PieUser user = null;
		ArrayList<PieUserEntity> entities;
		ArrayList<PieUser> models = new ArrayList<>();

		if (query.getResultList().isEmpty()) {
			return null;
		}

		entities = (ArrayList<PieUserEntity>) query.getResultList();

		entities.forEach((en) -> {
			try {
				models.add(modelEntityConverterService.convertFromEntity(en));
			}
			catch (Exception ex) {
				PieLogger.error(this.getClass(), "Error converting Entity", ex);
			}
		});
		return models;
	}

	@Override
	public void removePieUser(PieUser user) {
		PieUserEntity ent;
		ent = modelEntityConverterService.convertToEntity(user);
		remove(ent);
	}

	@Override
	public void mergePieUser(PieUser user) {
		PieUserEntity entity;
		entity = modelEntityConverterService.convertToEntity(user);
		merge(entity);
	}

	@Override
	public void persistFileFilter(IFilter filter) {
		FilterEntity en = null;
		en = modelEntityConverterService.convertToEntity(filter);
		persist(en);
	}

	@Override
	public void removeFileFilter(IFilter filter) {
		FilterEntity f;
		f = modelEntityConverterService.convertToEntity(filter);
		remove(f);
	}

	@Override
	public ArrayList<IFilter> findAllFilters() {
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(FilterEntity.class);
		Query query = em.createQuery(String.format("SELECT e FROM %s e", FilterEntity.class.getSimpleName()), FilterEntity.class);
		ArrayList<IFilter> list = new ArrayList<>();

		if (query.getResultList().isEmpty()) {
			return list;
		}

		for (FilterEntity entity : (Collection<FilterEntity>) query.getResultList()) {
			RegexFileFilter filter = modelEntityConverterService.convertFromEntity(entity);
			list.add(filter);
		}
		return list;
	}

	public void persist(PieFile file) {
		PieFileEntity entity;
		entity = this.modelEntityConverterService.convertToEntity(file);
		this.persist(entity);
	}

	private void persist(IBaseEntity entity) {
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(entity.getClass());
		em.getTransaction().begin();
		em.persist(entity);
		em.getTransaction().commit();
	}

	private void merge(IBaseEntity entity) {
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(entity.getClass());
		em.getTransaction().begin();
		em.merge(entity);
		em.getTransaction().commit();
	}

	private void remove(IBaseEntity entity) {
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(entity.getClass());
		em.getTransaction().begin();
		em.remove(entity);
		em.getTransaction().commit();
	}
}
