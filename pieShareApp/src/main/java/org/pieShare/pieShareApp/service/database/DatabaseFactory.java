/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieShareApp.service.configurationService.api.IApplicationConfigurationService;
import org.pieShare.pieShareApp.service.database.DAOs.FileFilterDAO;
import org.pieShare.pieShareApp.service.database.DAOs.PieFileDAO;
import org.pieShare.pieShareApp.service.database.DAOs.PieUserDAO;
import org.pieShare.pieShareApp.service.database.api.IPieDatabaseManagerFactory;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.AShutdownableService;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;

/**
 *
 * @author RicLeo00
 */
public class DatabaseFactory extends AShutdownableService implements IPieDatabaseManagerFactory, IShutdownableService {

    private final String DB_NAME = "pieShareDatabase";
    private String DB_Path = "";
    private Semaphore databaseSemaphore;
    private Semaphore countSemaphore;
    private int count;

    private IApplicationConfigurationService applicationConfigurationService;

    private Connection databseConnection;
    private DatabaseCreator creator;

    public void setCreator(DatabaseCreator creator) {
        this.creator = creator;
    }

    public void setApplicationConfigurationService(IApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public DatabaseFactory() {
        databaseSemaphore = new Semaphore(0);
        countSemaphore = new Semaphore(1);
        count = 0;
    }

    @Override
    public void init() {
        try {
            
            if(databseConnection != null && !databseConnection.isClosed())
            {
                closeDB();
            }
            
            File dbFolder = applicationConfigurationService.getDatabaseFolder();
            if(dbFolder != null )
                DB_Path = dbFolder.getCanonicalPath();
            else
                DB_Path = "";
            
            File dbFile = new File(String.format("%s%s%s", DB_Path, File.separator, DB_NAME));

            boolean createFirst = false;
            if (!dbFile.exists()) {
                createFirst = true;
            }

            ///Class.forName("org.sqlite.JDBC");
             Class.forName("org.hsqldb.jdbcDriver");
            databseConnection = DriverManager.getConnection(String.format("jdbc:hsqldb:/%s/%s", DB_Path, DB_NAME));

            if (createFirst) {
                creator.Create(this);
            }

        } catch (ClassNotFoundException ex) {
            PieLogger.error(this.getClass(), "SQL implementation not found.", ex);
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error creating databsase.", ex);
        } catch (IOException ex) {
            PieLogger.error(this.getClass(), "DB IO Error.", ex);
        }
    }

    @Override
    public Connection getDatabaseConnection() {
        try {
            countSemaphore.acquire();
            count++;
            countSemaphore.release();

            count--;
            return databseConnection;
        } catch (InterruptedException ex) {
            PieLogger.error(this.getClass(), "Error with database lock.", ex);
            return null;
        }
    }

    @Override
    public void closeDB() {
        try {
            countSemaphore.acquire();

            while (count > 0) {
                countSemaphore.release();
                Thread.sleep(500);
                countSemaphore.acquire();
            }
            databseConnection.close();

            countSemaphore.release();
        } catch (InterruptedException ex) {
            PieLogger.error(this.getClass(), "Error with database lock.", ex);
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error closing database.", ex);
        }
    }

    @Override
    public void shutdown() {
        closeDB();
    }
}
