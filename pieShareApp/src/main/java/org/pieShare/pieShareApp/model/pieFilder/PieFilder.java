/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.pieFilder;

import org.pieShare.pieShareApp.model.api.IBaseModel;
import java.util.UUID;
/**
 * Abstract PieFilder object
 * used by PieFile and PieFolder
 * @author daniela
 */
public abstract class PieFilder implements IBaseModel {
   
    private String id; 
    private String relativePath;
    private String name;
    private boolean deleted;
    
    public PieFilder() {
        this.deleted = false;
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public boolean isDeleted() {
	return deleted;
    }
    
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName (String name) {
        this.name = name;
    }
    
    public String getRelativePath() {
        return relativePath;
    }
    
    //relative path + name
    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }
}
