/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.pieShare.pieShareApp.model.model.entities.ConfigurationEntity;
import org.pieShare.pieShareApp.model.model.entities.PieUserEntity;
import org.pieShare.pieShareApp.service.database.api.IPieDatabaseManagerFactory;

/**
 *
 * @author RicLeo00
 */
public class ConfigurationDAO {

    private final String InsertIntoConfigutration = "INSERT INTO Configuration (ID, WorkingDir, TempDir, PwdFile) VALUES (?,?,?,?);";
    private final String UpdateConfigutration = "UPDATE Configuration SET WorkingDir=?, TempDir=?, PwdFile=? WHERE ID=?;";
    private final String DeleteConfigutration = "DELETE FROM Configuration WHERE ID=?";
    private final String FindByID = "SELECT * FROM Configuration WHERE ID=?;";

    private IPieDatabaseManagerFactory databaseFactory;

    public void setDatabaseFactory(IPieDatabaseManagerFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
    }

    public void saveConfiguration(ConfigurationEntity configurationEntity) throws SQLException {
        Connection con = databaseFactory.getDatabaseConnection();

        try (PreparedStatement insertInto = con.prepareStatement(InsertIntoConfigutration)) {
            insertInto.setString(1, configurationEntity.getUser());
            insertInto.setString(2, configurationEntity.getWorkingDir());
            insertInto.setString(3, configurationEntity.getTmpDir());
            insertInto.setString(4, configurationEntity.getPwdFile());
            insertInto.executeUpdate();
        }
    }

    public void updateConfiguration(ConfigurationEntity configurationEntity) throws SQLException {
        Connection con = databaseFactory.getDatabaseConnection();

        try (PreparedStatement updateQuery = con.prepareStatement(UpdateConfigutration)) {
            updateQuery.setString(1, configurationEntity.getWorkingDir());
            updateQuery.setString(2, configurationEntity.getTmpDir());
            updateQuery.setString(3, configurationEntity.getPwdFile());
            updateQuery.setString(4, configurationEntity.getUser());
            updateQuery.executeUpdate();
        }
    }

    public void deleteConfiguration(ConfigurationEntity configurationEntity) throws SQLException {
        Connection con = databaseFactory.getDatabaseConnection();

        try (PreparedStatement deleteQuery = con.prepareStatement(DeleteConfigutration)) {
            deleteQuery.setString(1, configurationEntity.getUser());
            deleteQuery.executeUpdate();
        }
    }

    public ConfigurationEntity findConfigurationById(String pieUser) throws SQLException {
        Connection con = databaseFactory.getDatabaseConnection();
        List<ConfigurationEntity> entities;
       
        try (PreparedStatement findByIDQuery = con.prepareStatement(FindByID)) {
            findByIDQuery.setString(1, pieUser);
            ResultSet results = findByIDQuery.executeQuery();
            entities = new ArrayList<>();
            while (results.next()) {
                ConfigurationEntity entity = new ConfigurationEntity();
                entity.setWorkingDir(results.getString("WorkingDir"));
                entity.setTmpDir(results.getString("TempDir"));
                entity.setPwdFile(results.getString("PwdFile"));
                entities.add(entity);
            }
        }

        if (entities.isEmpty()) {
            return null;
        }
        
        return entities.get(0);
    }
}
