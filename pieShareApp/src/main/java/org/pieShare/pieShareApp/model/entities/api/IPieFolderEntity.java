/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.entities.api;

/**
 * This interface holds all information needed to Sync
 * a PieFolder to the DB - needed for history service
 * 
 * @author daniela
 */
public interface IPieFolderEntity extends IBaseEntity{
    
    /**
     * Holds info whether a folder is already synced to DB.
     * @return true if file is in DB
     */
    boolean isSynced();
    
    /**
     * Set whether a folder is in the DB
     * @param synced true for file in DB, false otherwise.
     */
    void setSynced(boolean synced);
    
    //ID for the object
    //String getAbsoluteWorkingPath();

    //ID for the object
    //void setAbsoluteWorkingPath(String absoluteWorkingPath);

    /**
     * States if the local folder is deleted. 
     * @return true if the local file is deleted, false if it still exits.
     */
    boolean isDeleted();
    
    /**
     * Set the deleted state of a folder. Deleted means that the local folder doesn't exist anymore.
     * @param deleted true for a deleted folder, false for a still existing one.
     */
    void setDeleted(boolean deleted);
    
    //ID
    /**
     * The relative path of a folder from the working directory as String.
     * (including the name)
     * Is also the ID
     * @return the relative path of a folder
     */
    String getRelativeFolderPath();
    
    /**
     * Set the relative path of a folder from the working directory as String.
     * (including the name)
     * IS also the ID
     * @param relativeFolderPath the relative path of a folder = ID
     */
    void setRelativeFolderPath(String relativeFolderPath);
    
    /**
     * The name of the folder
     * @return folderName
     */
    String getFolderName();
    
    /**
     * Set the name of a folder
     * @param folderName 
     */
    void setFolderName (String folderName);
    
    
	long getLastModified();

	void setLastModified(long lastModified);
    
}
