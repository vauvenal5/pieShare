/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database.api;

import javax.persistence.EntityManager;

/**
 *
 * @author Richard
 */
public interface IPieDatabaseManagerFactory {

	EntityManager getEntityManger(Class clazz);

	void closeDB();

	void init();
}
