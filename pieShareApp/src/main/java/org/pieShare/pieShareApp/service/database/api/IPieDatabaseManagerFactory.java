/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database.api;

import java.sql.Connection;

/**
 *
 * @author Richard
 */
public interface IPieDatabaseManagerFactory {

    Connection getDatabaseConnection();

    void closeDB();

    void init();
}
