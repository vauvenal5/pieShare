/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.piespring.service.model.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import org.pieShare.pieShareApp.model.entities.api.IBaseEntity;
import org.pieShare.pieShareApp.model.entities.api.IConfigurationEntity;
import org.pieShare.pieShareApp.model.entities.api.IPieUserEntity;

/**
 *
 * @author Richy
 */
@Entity
public class PieUserEntity implements IPieUserEntity {

    @Id
    private String userName;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "pieUserEntity")
    private IConfigurationEntity configurationEntity;

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
        this.configurationEntity = configurationEntity;
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
    }
}
