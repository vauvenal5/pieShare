/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.codehaus.plexus.util.FileUtils;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.model.entities.BaseEntity;
import org.pieShare.pieShareApp.model.entities.FilterEntity;
import org.pieShare.pieShareApp.model.entities.PieUserEntity;
import org.pieShare.pieShareApp.service.configurationService.PieShareAppConfiguration;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;
import org.pieShare.pieShareApp.service.fileFilterService.filters.RegexFileFilter;
import org.pieShare.pieShareApp.service.fileFilterService.filters.api.IFilter;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.service.base64Service.api.IBase64Service;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 *
 * @author Richard Sveti toled me there is normaly a converter server, but i
 * mixed it, he sad it is o for now. But we have to document this in the bac.
 * thesis later.
 */
public class DatabaseService implements IDatabaseService {

	private PieShareAppConfiguration appConfiguration;
	private EntityManagerFactory emf;
	private IBase64Service base64Service;
	private IBeanService beanService;

	public void setBase64Service(IBase64Service base64Service) {
		this.base64Service = base64Service;
	}

	public void setPieShareAppConfiguration(PieShareAppConfiguration config) {
		this.appConfiguration = config;
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	@PostConstruct
	public void init() {
		//ToDo: Delete DB Hack
		String newDBDir = String.valueOf(new Date().getTime());

		File file = new File(String.format("%s%s", appConfiguration.getBaseConfigPath(), "/objectdb/db/points.odb"));
		File newFile = new File(String.format("%s/objectdb/db/%spoints.odb", appConfiguration.getBaseConfigPath(), newDBDir));
		if(file.exists())
		{
			try {
				FileUtils.copyFile(file, newFile);
			} catch (IOException ex) {
				Logger.getLogger(DatabaseService.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		emf = Persistence.createEntityManagerFactory(String.format("%s/objectdb/db/%spoints.odb", appConfiguration.getBaseConfigPath(), newDBDir));
	}

	@Override
	public void persistPieUser(PieUser service) {
		EntityManager em = emf.createEntityManager();
		PieUserEntity entity = new PieUserEntity();
		byte[] pwd = base64Service.encode(service.getPassword().getPassword());
		entity.setPassword(pwd);
		entity.setUserName(service.getUserName());
		em.getTransaction().begin();
		em.persist(entity);
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public PieUser getPieUser(String name) {
		EntityManager em = emf.createEntityManager();
		PieUser user = null;
		try {
			PieUserEntity entity = em.find(PieUserEntity.class, name);
			if (entity == null) {
				return null;
			}
			user = beanService.getBean(PieUser.class);
			user.setIsLoggedIn(false);
			EncryptedPassword paswd = new EncryptedPassword();
			paswd.setPassword(base64Service.decode(entity.getPassword()));
			user.setPassword(paswd);
			user.setUserName(entity.getUserName());
			em.close();
		} catch (IllegalArgumentException ex) {
			return null;
		}
		return user;
	}

	@Override
	public ArrayList<PieUser> findAllPieUsers() {
		EntityManager em = emf.createEntityManager();
		Query query = em.createQuery(String.format("SELECT e FROM %s e", PieUserEntity.class.getSimpleName()));

		ArrayList<PieUser> list = new ArrayList<>();
		for (PieUserEntity entity : ((Collection<PieUserEntity>) query.getResultList())) {
			PieUser user = beanService.getBean(PieUser.class);
			user.setIsLoggedIn(false);
			EncryptedPassword paswd = new EncryptedPassword();
			paswd.setPassword(base64Service.decode(entity.getPassword()));
			user.setPassword(paswd);
			user.setUserName(entity.getUserName());
			list.add(user);
		}
		em.close();
		return list;
	}

	@Override
	public void persistFileFilter(IFilter filter) {
		EntityManager em = emf.createEntityManager();

		//ToDo: Spring
		FilterEntity en = new FilterEntity();
		en.setPattern(filter.getPattern());
		filter.setEntity(en);
		persistBasicEntity(em, en);
	}

	@Override
	public void removeFileFilter(IFilter filter) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		FilterEntity f = em.find(FilterEntity.class, filter.getEntity().getId());
		em.remove(f);

		em.getTransaction().commit();
		em.close();
	}

	@Override
	public ArrayList<IFilter> findAllFilters() {
		EntityManager em = emf.createEntityManager();
		Query query = em.createQuery(String.format("SELECT e FROM %s e", FilterEntity.class.getSimpleName()));
		ArrayList<IFilter> list = new ArrayList<>();

		List resultList;

		try {
			resultList = query.getResultList();
		} catch (Exception ex) {
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
		em.close();
		return list;
	}

	private void persistBasicEntity(EntityManager em, BaseEntity basic) {
		em.getTransaction().begin();
		em.persist(basic);
		em.getTransaction().commit();
		em.close();
	}

}
