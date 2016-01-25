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
import org.pieShare.pieShareApp.model.entities.ConfigurationEntity;
import org.pieShare.pieShareApp.model.entities.FilterEntity;
import org.pieShare.pieShareApp.model.entities.PieUserEntity;
import org.pieShare.pieShareApp.service.database.api.IPieDatabaseManagerFactory;

/**
 *
 * @author RicLeo00
 */
public class FileFilterDAO {

    private final String InsertIntoFilter = "INSERT INTO Filter (Pattern) VALUES (?);";
    private final String DeleteFilter = "DELETE FROM Filter WHERE Pattern=?";
    private final String FindAll = "SELECT * FROM Filter;";

    private IPieDatabaseManagerFactory databaseFactory;

    public void setDatabaseFactory(IPieDatabaseManagerFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
    }

    public void saveFilter(FilterEntity filterEntity) throws SQLException {

        Connection con = databaseFactory.getDatabaseConnection();

        try (PreparedStatement insertInto = con.prepareStatement(InsertIntoFilter)) {
            insertInto.setString(1, filterEntity.getPattern());
            insertInto.executeUpdate();
        }
    }

    public void deleteFilter(FilterEntity filterEntity) throws SQLException {
        Connection con = databaseFactory.getDatabaseConnection();

        try (PreparedStatement deleteQuery = con.prepareStatement(DeleteFilter)) {
            deleteQuery.setString(1, filterEntity.getPattern());
            deleteQuery.executeUpdate();
        }
    }

    public List<FilterEntity> findAllFilter() throws SQLException {
        Connection con = databaseFactory.getDatabaseConnection();
        List<FilterEntity> entities;

        try (PreparedStatement findAllQuery = con.prepareStatement(FindAll)) {
            ResultSet results = findAllQuery.executeQuery();
            entities = new ArrayList<>();

            while (results.next()) {
                FilterEntity entity = new FilterEntity();
                entity.setPattern(results.getString("Pattern"));
                entities.add(entity);
            }
        }
        return entities;
    }
}
