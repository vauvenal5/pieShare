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
import org.pieShare.pieShareApp.model.entities.PieFolderEntity;
import org.pieShare.pieShareApp.service.database.api.IPieDatabaseManagerFactory;

/**
 *
 * @author RicLeo00
 */
public class PieFolderDAO {

    private final String InsertPieFolder = "INSERT INTO PieFolder (ID, FileName, LastModified, Deleted, Synched, Parent, IsRoot) VALUES (?,?,?,?,?,?,?);";
    private final String SetAllSyncedFalse = "UPDATE PieFolder SET Synched=0 WHERE Synched=1;";
    private final String FindAll = "SELECT * FROM PieFolder;";
    private final String FindAllUnsyched = "SELECT * FROM PieFolder WHERE Synched=0;";
    private final String FindByID = "SELECT * FROM PieFolder WHERE ID=?;";
    private final String UpdatePieFolder = "UPDATE PieFolder SET FileName=?, LastModified=?, Deleted=?, Synched=? WHERE ID=?;";
    private final String DeletePieFile = "DELETE FROM PieFolder WHERE ID=?";

    private final String RenameFolder =  "UPDATE PieFolder SET FileName=? WHERE ID=?;";
    private final String GetFolderWhereRootAndParent = "SELECT * FROM PieFolder WHERE IsRoot=? AND FileName=? AND Parent=?;";
     private final String GetFolderWhereNameAndParent = "SELECT * FROM PieFolder WHERE FileName=? AND Parent=?;";

    private IPieDatabaseManagerFactory databaseFactory;

    public void setDatabaseFactory(IPieDatabaseManagerFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
    }

    public void savePiePieFolder(PieFolderEntity pieFolderEntity) throws SQLException {

        Connection con = databaseFactory.getDatabaseConnection();

        try (PreparedStatement insertInto = con.prepareStatement(InsertPieFolder)) {
            insertInto.setString(1, pieFolderEntity.getId());
            insertInto.setString(2, pieFolderEntity.getFolderName());
            insertInto.setLong(3, pieFolderEntity.getLastModified());

            int val = 0;
            if (pieFolderEntity.isDeleted()) {
                val = 1;
            }
            insertInto.setInt(4, val);

            val = 0;
            if (pieFolderEntity.isSynced()) {
                val = 1;
            }
            insertInto.setInt(5, val);
            insertInto.setString(6, pieFolderEntity.getParent());
            insertInto.setBoolean(7, pieFolderEntity.isIsRoot());
            insertInto.executeUpdate();
        }
    }

    public void updatePieFolder(PieFolderEntity pieFolderEntity) throws SQLException {

        Connection con = databaseFactory.getDatabaseConnection();

        try (PreparedStatement updateQuery = con.prepareStatement(UpdatePieFolder)) {
            updateQuery.setString(1, pieFolderEntity.getFolderName());
	
            updateQuery.setLong(2, pieFolderEntity.getLastModified());

            int val = 0;
            if (pieFolderEntity.isDeleted()) {
                val = 1;
            }
            updateQuery.setInt(3, val);

            val = 0;
            if (pieFolderEntity.isSynced()) {
                val = 1;
            }
            updateQuery.setInt(4, val);

            updateQuery.setString(5, pieFolderEntity.getId());
            updateQuery.executeUpdate();
        }
    }

    public void resetAllPieFolderSynchedFlags() throws SQLException {
        Connection con = databaseFactory.getDatabaseConnection();

        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(SetAllSyncedFalse);
        }
    }

    public void deletePieFolder(String ID) throws SQLException {
        Connection con = databaseFactory.getDatabaseConnection();

        try (PreparedStatement updateQuery = con.prepareStatement(DeletePieFile)) {
            updateQuery.setString(1, ID);
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
    
   public void renameFolder(String ID, String newName) throws SQLException {
        Connection con = databaseFactory.getDatabaseConnection();
        
        try (PreparedStatement renameQuery = con.prepareStatement(GetFolderWhereRootAndParent)) {
            renameQuery.setString(1, newName);
            renameQuery.setString(2, ID);
            renameQuery.executeUpdate();
        }
   }

    public PieFolderEntity findFolderWhereNameANDIsRoot(String name, boolean isRoot, String parent) throws SQLException {
        Connection con = databaseFactory.getDatabaseConnection();
        List<PieFolderEntity> entities;

        try (PreparedStatement findAllQuery = con.prepareStatement(GetFolderWhereRootAndParent)) {
            findAllQuery.setBoolean(1, isRoot);
            findAllQuery.setString(2, name);
            findAllQuery.setString(3, parent);
            ResultSet results = findAllQuery.executeQuery();
            entities = createFromResult(results);
        }
        
        if(entities.isEmpty())
            return null;
        
        return entities.get(0);
    }
    
     public PieFolderEntity findFolderWhereNameAndParent(String name, String parent) throws SQLException {
        Connection con = databaseFactory.getDatabaseConnection();
        List<PieFolderEntity> entities;

        try (PreparedStatement findAllQuery = con.prepareStatement(GetFolderWhereNameAndParent)) {
            findAllQuery.setString(1, name);
            findAllQuery.setString(2, parent);
            ResultSet results = findAllQuery.executeQuery();
            entities = createFromResult(results);
        }
        
        if(entities.isEmpty())
            return null;
        
        return entities.get(0);
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
            entity.setId(results.getString("ID"));
            entity.setFolderName(results.getString("FileName"));
            entity.setLastModified(results.getLong("LastModified"));
            entity.setDeleted(results.getInt("Deleted") != 0);
            entity.setSynced(results.getInt("Synched") != 0);
            entity.setParent(results.getString("Parent"));
            entity.setIsRoot(results.getBoolean("IsRoot"));
            entities.add(entity);
        }

        return entities;
    }
}
