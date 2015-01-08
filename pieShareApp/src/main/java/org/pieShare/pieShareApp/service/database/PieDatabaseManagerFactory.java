/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jgroups.util.BoundedHashMap;
import org.pieShare.pieShareApp.service.configurationService.api.IApplicationConfigurationService;
import org.pieShare.pieShareApp.service.database.api.IPieDatabaseManagerFactory;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;

/**
 *
 * @author Richard
 */
public class PieDatabaseManagerFactory implements IPieDatabaseManagerFactory, IShutdownableService {

	private IApplicationConfigurationService appConfiguration;
	private EntityManagerFactory emf;
	private HashMap<Class, EntityManager> entityManagers;
	private Semaphore databaseSemaphore;
	private Semaphore countSemaphore;
	private int count;

	public PieDatabaseManagerFactory() {
		entityManagers = new HashMap<>();
		databaseSemaphore = new Semaphore(0);
		countSemaphore = new Semaphore(1);
		count = 0;
	}

	public void setApplicationConfigurationService(IApplicationConfigurationService config) {
		this.appConfiguration = config;
	}

	@PostConstruct
	@Override
	public void init() {
		emf = Persistence.createEntityManagerFactory(String.format("%s/database.odb", appConfiguration.getDatabaseFolder().toPath().toString()));
		countSemaphore.release();
	}

	@Override
	public void closeDB() {
		try {
			countSemaphore.acquire();

			while (count > 0) {
				Thread.sleep(500);
			}

			entityManagers.entrySet().stream().forEach((entity) -> {
				entity.getValue().close();
			});
			entityManagers.clear();
			emf.close();
		}
		catch (InterruptedException ex) {
			PieLogger.error(this.getClass(), "Error with database lock.", ex);
		}
	}

	@Override
	public EntityManager getEntityManger(Class clazz) {
		try {
			countSemaphore.acquire();
			count++;
			countSemaphore.release();

			if (entityManagers.containsKey(clazz)) {
				count--;
				return entityManagers.get(clazz);
			}

			EntityManager manager = emf.createEntityManager();
			entityManagers.put(clazz, manager);

			//countSemaphore.acquire();
			//countSemaphore.release();
			count--;
			return manager;
		}
		catch (InterruptedException ex) {
			PieLogger.error(this.getClass(), "Error with database lock.", ex);
			return null;
		}
	}

	@Override
	public void shutdown() {
		this.closeDB();
	}

}
