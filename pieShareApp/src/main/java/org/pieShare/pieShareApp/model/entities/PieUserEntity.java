/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.entities;

/**
 *
 * @author Richy
 */
public class PieUserEntity {

    private String userName;

    private ConfigurationEntity configurationEntity;

    private boolean hasPasswordFile;

    public boolean isHasPasswordFile() {
        return hasPasswordFile;
    }

    public ConfigurationEntity getConfigurationEntity() {
        return configurationEntity;
    }

    public void setConfigurationEntity(ConfigurationEntity configurationEntity) {
        this.configurationEntity = (ConfigurationEntity)configurationEntity;
    }

    public void setHasPasswordFile(boolean hasPasswordFile) {
        this.hasPasswordFile = hasPasswordFile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
