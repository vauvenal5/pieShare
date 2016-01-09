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
            + "	ID VARCHAR PRIMARY KEY,"
            + "	UserName VARCHAR,"
            + "	ConfigurationID VARCHAR,"
            + "	HasPasswordFile INTEGER"
            + ");";

    private final String createConfigurationSql = "CREATE TABLE Configuration ( "
            + "	ID VARCHAR PRIMARY KEY,"
            + "	WorkingDir VARCHAR,"
            + "	TempDir VARCHAR,"
            + "	PwdFile VARCHAR"
            + ");";

    private final String createFilterSql = "CREATE TABLE Filter ( "
            + "	Pattern VARCHAR PRIMARY KEY"
            + ");";

    private final String createPieFile = "CREATE TABLE PieFile ( "
            + "	RelativeFilePath VARCHAR PRIMARY KEY,"
            + "	FileName VARCHAR,"
            + "	LastModified REAL,"
            + "	Deleted INTEGER,"
            + "	Synched INTEGER,"
            + "	MD5 BINARY"
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
