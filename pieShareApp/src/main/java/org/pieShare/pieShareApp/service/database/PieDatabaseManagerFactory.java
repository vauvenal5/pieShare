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
import org.pieShare.pieShareApp.service.configurationService.PieShareAppConfiguration;
import org.pieShare.pieShareApp.service.database.api.IPieDatabaseManagerFactory;

/**
 *
 * @author Richard
 */
public class PieDatabaseManagerFactory implements IPieDatabaseManagerFactory {

	private PieShareAppConfiguration appConfiguration;
	private EntityManagerFactory emf;
	private HashMap<Class, EntityManager> entityManagers;

	public PieDatabaseManagerFactory() {
	}

	public void setPieShareAppConfiguration(PieShareAppConfiguration config) {
		this.appConfiguration = config;
	}

	@PostConstruct
	public void init() {
		emf = Persistence.createEntityManagerFactory(String.format("%s/objectdb/db/points.odb", appConfiguration.getBaseConfigPath()));
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
