/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.model.entities;

import org.pieShare.pieShareApp.model.entities.api.IPieFolderEntity;


/**
 *
 * @author daniela
 */
public class PieFolderEntity implements IPieFolderEntity{
    private boolean synced;
    private boolean deleted;
    private String relativeFolderPath;
    private String folderName;
    
    public PieFolderEntity() {
        this.synced = true;
    }
    
    @Override
    public boolean isSynced() {
        return synced;
    }

    @Override
    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String getRelativeFolderPath() {
        return relativeFolderPath;
    }

    @Override
    public void setRelativeFolderPath(String relativeFolderPath) {
        this.relativeFolderPath = relativeFolderPath;
    }

    @Override
    public String getFolderName() {
        return folderName;
    }

    @Override
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
