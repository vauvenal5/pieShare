/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.api.IBaseModel;
import org.pieShare.pieShareApp.model.entities.BaseEntity;
import org.pieShare.pieShareApp.model.entities.FilterEntity;
import org.pieShare.pieShareApp.model.entities.PieFileEntity;
import org.pieShare.pieShareApp.model.entities.PieUserEntity;
import org.pieShare.pieShareApp.model.entities.api.IBaseEntity;
import org.pieShare.pieShareApp.model.pieFile.PieFile;
import org.pieShare.pieShareApp.service.configurationService.api.IConfigurationFactory;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.service.database.api.IModelEntityConverterService;
import org.pieShare.pieShareApp.service.database.api.IPieDatabaseManagerFactory;
import org.pieShare.pieShareApp.service.database.exception.NotConvertableException;
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
		try {
			entity = this.modelEntityConverterService.convertToEntity(model);
		}
		catch (NotConvertableException ex) {
			PieLogger.error(this.getClass(), "Error convertig model to entity", ex);
			return;
		}
		persist(entity);
	}

	@Override
	public ArrayList<PieUser> findAllPieUser() {
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(PieUserEntity.class);

		Query query = em.createQuery(String.format("SELECT e FROM %s e", PieUserEntity.class.getSimpleName()));
		PieUser user = null;
		ArrayList<PieUserEntity> entities;
		ArrayList<PieUser> models = new ArrayList<>();

		try {
			entities = (ArrayList<PieUserEntity>) query.getResultList();
		}
		catch (Exception ex) {
			return null;
		}

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
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(PieUserEntity.class);
		PieUserEntity ent;
		try {
			ent = modelEntityConverterService.convertToEntity(user);
		}
		catch (NotConvertableException ex) {
			PieLogger.error(this.getClass(), "Error converting Entity", ex);
			return;
		}
		remove(ent);
	}

	@Override
	public void mergePieUser(PieUser user) {
		PieUserEntity entity;
		try {
			entity = modelEntityConverterService.convertToEntity(user);
		}
		catch (NotConvertableException ex) {
			PieLogger.error(this.getClass(), "Error removing User from DB", ex);
			return;
		}
		merge(entity);
	}

	@Override
	public void persistFileFilter(IFilter filter) {
		FilterEntity en = null;
		try {
			en = modelEntityConverterService.convertToEntity(filter);
		}
		catch (NotConvertableException ex) {
			PieLogger.error(this.getClass(), "Error converting Entity", ex);
			return;
		}
		persist(en);
	}

	@Override
	public void removeFileFilter(IFilter filter) {
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(FilterEntity.class);
		em.getTransaction().begin();

		FilterEntity f = em.find(FilterEntity.class, filter.getEntity().getId());
		em.remove(f);

		em.getTransaction().commit();
	}

	@Override
	public ArrayList<IFilter> findAllFilters() {
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(FilterEntity.class);
		Query query = em.createQuery(String.format("SELECT e FROM %s e", FilterEntity.class.getSimpleName()));
		ArrayList<IFilter> list = new ArrayList<>();

		List resultList;

		try {
			resultList = query.getResultList();
		}
		catch (Exception ex) {
			return list;
		}

		if (!resultList.isEmpty()) {
			for (FilterEntity entity : (Collection<FilterEntity>) resultList) {
				IFilter filter = beanService.getBean(RegexFileFilter.class);
				filter.setEntity(entity);
				filter.setPattern(entity.getPattern());
				list.add(filter);
			}
		}
		return list;
	}

	//@Override
	public void persist(PieFile file) {
		PieFileEntity entity;
		try {
			entity = this.modelEntityConverterService.convertToEntity(file);
		}
		catch (NotConvertableException ex) {
			PieLogger.error(this.getClass(), "Error converting Entity", ex);
			return;
		}
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

	@Override
	public PieFile findPieFile(PieFile file) {
		File idFile = new File(file.getRelativeFilePath(), file.getFileName());
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(PieFileEntity.class);
		PieFileEntity historyFileEntity = em.find(PieFileEntity.class, idFile.getPath());
		return this.modelEntityConverterService.convertFromEntity(historyFileEntity);
	}

}
