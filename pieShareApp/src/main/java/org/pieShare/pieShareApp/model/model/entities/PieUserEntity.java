/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.model.entities;

import org.pieShare.pieShareApp.model.entities.api.IConfigurationEntity;
import org.pieShare.pieShareApp.model.entities.api.IPieUserEntity;

/**
 *
 * @author Richy
 */
public class PieUserEntity implements IPieUserEntity {

    private String userName;

    private ConfigurationEntity configurationEntity;

    private boolean hasPasswordFile;

    @Override
    public boolean isHasPasswordFile() {
        return hasPasswordFile;
    }

    @Override
    public IConfigurationEntity getConfigurationEntity() {
        return configurationEntity;
    }

    @Override
    public void setConfigurationEntity(IConfigurationEntity configurationEntity) {
        this.configurationEntity = (ConfigurationEntity)configurationEntity;
    }

    @Override
    public void setHasPasswordFile(boolean hasPasswordFile) {
        this.hasPasswordFile = hasPasswordFile;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
        
        
    String sql = "CREATE TABLE Person ( "
                + "	PersonID int,"
                + "	LastName varchar(255),"
                + "	FirstName varchar(255),"
                + "	Address varchar(255),"
                + "	City varchar(255)"
                + ");";
        
        
    }
}
