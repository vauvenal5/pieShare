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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.pieShare.pieShareApp.model.model.entities.PieFileEntity;
import org.pieShare.pieShareApp.model.model.entities.PieFolderEntity;
import org.pieShare.pieShareApp.service.database.api.IPieDatabaseManagerFactory;

/**
 *
 * @author RicLeo00
 */
public class PieFolderDAO {

    private final String InsertPieFolder = "INSERT INTO PieFolder (RelativeFilePath, FileName, Deleted, Synched) VALUES (?,?,?,?);";
    private final String SetAllSyncedFalse = "UPDATE PieFolder SET Synched=0 WHERE Synched=1;";
    private final String FindAll = "SELECT * FROM PieFolder;";
    private final String FindAllUnsyched = "SELECT * FROM PieFolder WHERE Synched=0;";
    private final String FindByID = "SELECT * FROM PieFolder WHERE RelativeFilePath=?;";
    private final String UpdatePieFolder = "UPDATE PieFolder SET FileName=?, Deleted=?, Synched=? WHERE RelativeFilePath=?;";
    private final String DeletePieFile = "DELETE FROM PieFolder WHERE RelativeFilePath=?";

    private IPieDatabaseManagerFactory databaseFactory;

    public void setDatabaseFactory(IPieDatabaseManagerFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
    }

    public void savePiePieFolder(PieFolderEntity pieFolderEntity) throws SQLException {

        Connection con = databaseFactory.getDatabaseConnection();

        try (PreparedStatement insertInto = con.prepareStatement(InsertPieFolder)) {
            insertInto.setString(1, pieFolderEntity.getRelativeFolderPath());
            insertInto.setString(2, pieFolderEntity.getFolderName());

            int val = 0;
            if (pieFolderEntity.isDeleted()) {
                val = 1;
            }
            insertInto.setInt(3, val);

            val = 0;
            if (pieFolderEntity.isSynced()) {
                val = 1;
            }
            insertInto.setInt(4, val);
            insertInto.executeUpdate();
        }
    }

    public void updatePieFolder(PieFolderEntity pieFolderEntity) throws SQLException {

        Connection con = databaseFactory.getDatabaseConnection();

        try (PreparedStatement updateQuery = con.prepareStatement(UpdatePieFolder)) {
            updateQuery.setString(1, pieFolderEntity.getFolderName());

            int val = 0;
            if (pieFolderEntity.isDeleted()) {
                val = 1;
            }
            updateQuery.setInt(2, val);

            val = 0;
            if (pieFolderEntity.isSynced()) {
                val = 1;
            }
            updateQuery.setInt(3, val);

            updateQuery.setString(4, pieFolderEntity.getRelativeFolderPath());
            updateQuery.executeUpdate();
        }
    }

    public void resetAllPieFolderSynchedFlags() throws SQLException {
        Connection con = databaseFactory.getDatabaseConnection();

        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(SetAllSyncedFalse);
        }
    }

    public void deletePieFolder(String relativeFilePath) throws SQLException {
        Connection con = databaseFactory.getDatabaseConnection();

        try (PreparedStatement updateQuery = con.prepareStatement(DeletePieFile)) {
            updateQuery.setString(1, relativeFilePath);
            updateQuery.executeUpdate();
        }
    }

    public PieFolderEntity findPieFolderById(String relativeFilePath) throws SQLException {

        Connection con = databaseFactory.getDatabaseConnection();
        List<PieFolderEntity> entities;

        try (PreparedStatement findById = con.prepareStatement(FindByID)) {
            findById.setString(1, relativeFilePath);
            ResultSet results = findById.executeQuery();
            entities = createFromResult(results);
        }
        if (entities.isEmpty()) {
            return null;
        }

        return entities.get(0);
    }

    public List<PieFolderEntity> findAllUnsyncedPieFolders() throws SQLException {
        return findAllSQL(FindAllUnsyched);
    }

    public List<PieFolderEntity> findAllPieFolders() throws SQLException {
        return findAllSQL(FindAll);
    }

    private List<PieFolderEntity> findAllSQL(String sql) throws SQLException {
        Connection con = databaseFactory.getDatabaseConnection();
        List<PieFolderEntity> entities;

        try (PreparedStatement findAllQuery = con.prepareStatement(sql)) {
            ResultSet results = findAllQuery.executeQuery();
            entities = createFromResult(results);
        }
        return entities;
    }

    private List<PieFolderEntity> createFromResult(ResultSet results) throws SQLException {
        List<PieFolderEntity> entities;
        entities = new ArrayList<>();

        while (results.next()) {
            PieFolderEntity entity = new PieFolderEntity();
            entity.setRelativeFolderPath(results.getString("RelativeFilePath"));
            entity.setFolderName(results.getString("FileName"));
            entity.setDeleted(results.getInt("Deleted") != 0);
            entity.setSynced(results.getInt("Synched") != 0);
            entities.add(entity);
        }

        return entities;
    }
}
