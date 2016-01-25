/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
            + "	ID VARCHAR PRIMARY KEY,"
            + "	FileName VARCHAR,"
            + "	LastModified REAL,"
            + "	Deleted INTEGER,"
            + "	Synched INTEGER,"
            + "	MD5 BINARY,"
            + "	Parent VARCHAR"
            + ");";

    private final String createPieFolder = "CREATE TABLE PieFolder ( "
            + "	ID VARCHAR PRIMARY KEY,"
            + "	FileName VARCHAR,"
			+ "	LastModified REAL,"
            + "	Deleted INTEGER,"
            + "	Synched INTEGER,"
            + "	Parent VARCHAR,"
            + "	IsRoot BOOLEAN"
            + ");";

    public void Create(DatabaseFactory fac) {
        Connection connection = fac.getDatabaseConnection();

        try {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(createUserSql);
                stmt.executeUpdate(createConfigurationSql);
                stmt.executeUpdate(createFilterSql);
                stmt.executeUpdate(createPieFile);
                stmt.executeUpdate(createPieFolder);
            }
        } catch (SQLException ex) {
            PieLogger.error(this.getClass(), "Error creating database!", ex);
        }

    }

}
