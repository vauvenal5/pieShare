/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author RicLeo00
 */
public class DatabaseCreator {

    private final String createUserSql = "CREATE TABLE User ( "
            + "	ID TEXT PRIMARY KEY,"
            + "	UserName TEXT,"
            + "	ConfigurationID INTEGER,"
            + "	HasPasswordFile INTEGER"
            + ");";

    private final String createConfigurationSql = "CREATE TABLE Configuration ( "
            + "	ID TEXT PRIMARY KEY,"
            + "	WorkingDir TEXT,"
            + "	TempDir TEXT,"
            + "	PwdFile TEXT"
            + ");";

    private final String createFilterSql = "CREATE TABLE Filter ( "
            + "	Pattern TEXT PRIMARY KEY"
            + ");";

    private final String createPieFile = "CREATE TABLE PieFile ( "
            + "	AbsoluteWorkingPath TEXT PRIMARY KEY,"
            + "	RelativeFilePath TEXT,"
            + "	FileName TEXT,"
            + "	LastModified REAL,"
            + "	Deleted INTEGER,"
            + "	Synched INTEGER,"
            + "	MD5 BLOB"
            + ");";

    public void Create(DatabaseFactory fac) {
        Connection connection = fac.getDatabaseConnection();

        try {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(createUserSql);
                stmt.executeUpdate(createConfigurationSql);
                stmt.executeUpdate(createFilterSql);
                stmt.executeUpdate(createPieFile);
            }
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error creating database!", ex);
        }

    }

}
