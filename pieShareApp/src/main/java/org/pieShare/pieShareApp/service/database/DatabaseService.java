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
import org.pieShare.pieShareApp.model.entities.BaseEntity;
import org.pieShare.pieShareApp.model.entities.FilterEntity;
import org.pieShare.pieShareApp.model.entities.PieFileEntity;
import org.pieShare.pieShareApp.model.entities.PieUserEntity;
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

/**
 *
 * @author Richard Sveti toled me there is normaly a converter server, but i
 * mixed it, he sad it is o for now. But we have to document this in the bac.
 * thesis later.
 */
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
	public void persistPieUser(PieUser user) {
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(PieUserEntity.class);
		PieUserEntity entity = modelEntityConverterService.userToEntity(user);
		em.getTransaction().begin();
		em.persist(entity);
		em.getTransaction().commit();
	}

	@Override
	public void mergePieUser(PieUser user) {
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(PieUserEntity.class);
		PieUserEntity entity = modelEntityConverterService.userToEntity(user);
		em.getTransaction().begin();
		em.merge(entity);
		em.getTransaction().commit();
	}

	@Override
	public PieUser getPieUser(String name) {
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(PieUserEntity.class);
		PieUser user = null;
		try {
			PieUserEntity entity = em.find(PieUserEntity.class, name);
			if (entity == null) {
				return null;
			}
			user = modelEntityConverterService.entityToUser(entity);
		}
		catch (IllegalArgumentException ex) {
			return null;
		}
		return user;
	}

	@Override
	public PieUser findPieUser() {
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(PieUserEntity.class);
		Query query = em.createQuery(String.format("SELECT e FROM %s e", PieUserEntity.class.getSimpleName()));

		PieUser user = null;
		ArrayList<PieUserEntity> entities;

		try {
			entities = (ArrayList<PieUserEntity>) query.getResultList();
		}
		catch (Exception ex) {
			return null;
		}

		if (entities != null && entities.size() > 0) {
			PieUserEntity entity = entities.get(0);
			user = modelEntityConverterService.entityToUser(entity);
		}
		return user;
	}

	@Override
	public void removePieUser(PieUser user) {
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(PieUserEntity.class);
		PieUserEntity ent = modelEntityConverterService.userToEntity(user);

		//ToDo: Check Delete
		//	= new PieUserEntity();//em.find(PieUserEntity.class, user.getUserName());
		//ent.setUserName(user.getCloudName());
		try {
			em.getTransaction().begin();
			em.remove(ent);
			em.getTransaction().commit();
		}
		catch (Exception ex) {
			PieLogger.error(this.getClass(), "Error removing User from DB", ex);
		}
	}

	@Override
	public void persistFileFilter(IFilter filter) {
		FilterEntity en = modelEntityConverterService.filterToFilterEntity(filter);
		persistBasicEntity(FilterEntity.class, en);
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

	private void persistBasicEntity(Class clazz, BaseEntity basic) {
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(clazz);
		em.getTransaction().begin();
		em.persist(basic);
		em.getTransaction().commit();
	}

	@Override
	public void persist(PieFile file) {
		//todo: all this can be abstracted by one single function for all default persists
		//BaseClass for all models
		//ConverterService.convertToEntity(BaseClass) 
		//	--> throws Exception
		//	--> overloads for all other types
		//persist(BaseClass) --> tada everything gets persisted by one function
		//and exception gets thrown if converter can't convert
		PieFileEntity entity = this.modelEntityConverterService.convertToEntity(file);
		EntityManager em = emf.createEntityManager();
		this.persistBasicEntity(PieFileEntity.class, entity);
	}

	@Override
	public <T extends BaseEntity> T findEntity(Class<T> clazz, Object key) {
		EntityManager em = pieDatabaseManagerFactory.getEntityManger(clazz);
		em.getTransaction().begin();
		T entity = em.find(clazz, key);
		em.getTransaction().commit();
		return entity;
	}
}
