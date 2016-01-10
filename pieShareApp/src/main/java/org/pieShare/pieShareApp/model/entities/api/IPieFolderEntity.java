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
    
    //holds info whether there are changes and an according msg has already been sent
    boolean isSynced();
    void setSynced(boolean synced);
    
    //ID for the object
    //String getAbsoluteWorkingPath();

    //ID for the object
    //void setAbsoluteWorkingPath(String absoluteWorkingPath);

    boolean isDeleted();
    
    void setDeleted(boolean deleted);
    
    //ID
    String getRelativeFolderPath();
    
    void setRelativeFolderPath(String relativeFolderPath);
    
    String getFolderName();
    
    void setFolderName (String folderName);
    
    

    
}
