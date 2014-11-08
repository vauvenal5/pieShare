/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database;

import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jgroups.util.BoundedHashMap;
import org.pieShare.pieShareApp.service.configurationService.api.IApplicationConfigurationService;
import org.pieShare.pieShareApp.service.database.api.IPieDatabaseManagerFactory;

/**
 *
 * @author Richard
 */
public class PieDatabaseManagerFactory implements IPieDatabaseManagerFactory {

	private IApplicationConfigurationService appConfiguration;
	private EntityManagerFactory emf;
	private HashMap<Class, EntityManager> entityManagers;

	public PieDatabaseManagerFactory() {
		entityManagers = new HashMap<>();
	}

	public void setApplicationConfigurationService(IApplicationConfigurationService config) {
		this.appConfiguration = config;
	}

	@PostConstruct
	public void init() {
		emf = Persistence.createEntityManagerFactory(String.format("%s/points.odb", appConfiguration.getDatabaseFolder().toPath().toString()));
	}

	@Override
	public EntityManager getEntityManger(Class clazz) {
		if (entityManagers.containsKey(clazz)) {
			return entityManagers.get(clazz);
		}
		EntityManager manager = emf.createEntityManager();
		entityManagers.put(clazz, manager);
		return manager;
	}

}
