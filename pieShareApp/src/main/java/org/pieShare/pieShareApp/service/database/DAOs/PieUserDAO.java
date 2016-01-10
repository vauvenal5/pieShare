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
public class PieUserDAO {

    private final String InsertIntoUser = "INSERT INTO User (ID, UserName, ConfigurationID, HasPasswordFile) VALUES (?,?,?,?);";
    private final String UpdateUser = "UPDATE User SET UserName=?, ConfigurationID=?, HasPasswordFile=? WHERE ID=?;";
    private final String DeleteUser = "DELETE FROM User WHERE ID=?";
    private final String FindByID = "SELECT * FROM User WHERE ID=?;";
    private final String FindAll = "SELECT * FROM User;";

    private IPieDatabaseManagerFactory databaseFactory;
    private ConfigurationDAO configurationDAO;

    public void setDatabaseFactory(IPieDatabaseManagerFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
    }

    public void setConfigurationDAO(ConfigurationDAO configurationDAO) {
        this.configurationDAO = configurationDAO;
    }

    public void savePieUser(PieUserEntity pieUserEntity) throws SQLException {

        Connection con = databaseFactory.getDatabaseConnection();

        try (PreparedStatement insertInto = con.prepareStatement(InsertIntoUser)) {
            insertInto.setString(1, pieUserEntity.getUserName());
            insertInto.setString(2, pieUserEntity.getUserName());
            insertInto.setString(3, pieUserEntity.getUserName());
            
            int val = 0;
            if (pieUserEntity.isHasPasswordFile()) {
                val = 1;
            }
            insertInto.setInt(4, val);
            insertInto.executeUpdate();
        }
        
        configurationDAO.saveConfiguration((ConfigurationEntity) pieUserEntity.getConfigurationEntity());
    }

    public void updatePieUser(PieUserEntity pieUserEntity) throws SQLException {

        Connection con = databaseFactory.getDatabaseConnection();

        try (PreparedStatement insertInto = con.prepareStatement(UpdateUser)) {
            insertInto.setString(1, pieUserEntity.getUserName());
            insertInto.setString(2, pieUserEntity.getUserName());
            
            int val = 0;
            if (pieUserEntity.isHasPasswordFile()) {
                val = 1;
            }
            insertInto.setInt(3, val);
            insertInto.setString(4, pieUserEntity.getUserName());
            insertInto.executeUpdate();
        }
        
        configurationDAO.updateConfiguration((ConfigurationEntity) pieUserEntity.getConfigurationEntity());
    }

    public void deletePieUser(PieUserEntity pieUserEntity) throws SQLException {

        Connection con = databaseFactory.getDatabaseConnection();

        try (PreparedStatement deleteQuery = con.prepareStatement(DeleteUser)) {
            deleteQuery.setString(1, pieUserEntity.getUserName());
            deleteQuery.executeUpdate();
        }
        
        configurationDAO.deleteConfiguration((ConfigurationEntity) pieUserEntity.getConfigurationEntity());
    }

    public PieUserEntity findPieUserById(String pieUser) throws SQLException {

        Connection con = databaseFactory.getDatabaseConnection();
        List<PieUserEntity> entities;
        
        try (PreparedStatement findByIDQuery = con.prepareStatement(FindByID)) {
            findByIDQuery.setString(1, pieUser);
            ResultSet results = findByIDQuery.executeQuery();
            entities = new ArrayList<>();
            while (results.next()) {
                PieUserEntity entity = new PieUserEntity();
                entity.setUserName(results.getString("ID"));
                
                int val = results.getInt("HasPasswordFile");
                entity.setHasPasswordFile(val != 0);
                
                entity.setConfigurationEntity(configurationDAO.findConfigurationById(pieUser));
                
                entities.add(entity);
            }
        }
        
        if (entities.isEmpty()) {
            return null;
        }
        return entities.get(0);
    }

    public List<PieUserEntity> findAllPieUsers() throws SQLException {
        Connection con = databaseFactory.getDatabaseConnection();
        List<PieUserEntity> entities;

        try (PreparedStatement findAllQuery = con.prepareStatement(FindAll)) {
            ResultSet results = findAllQuery.executeQuery();
            entities = new ArrayList<>();
           
            while (results.next()) {
                PieUserEntity entity = new PieUserEntity();
                entity.setUserName(results.getString("ID"));
                
                int val = results.getInt("HasPasswordFile");
                entity.setHasPasswordFile(val != 0);
                
                entity.setConfigurationEntity(configurationDAO.findConfigurationById(entity.getUserName()));
                
                entities.add(entity);
            }
        }
        return entities;
    }

}
