/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.entities;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import org.pieShare.pieShareApp.model.entities.api.IBaseEntity;

/**
 *
 * @author Richy
 */
@Entity
public class PieUserEntity implements IBaseEntity {

	@Id
	private String userName;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "pieUserEntity")
	private ConfigurationEntity configurationEntity;

	private boolean hasPasswordFile;

	public boolean isHasPasswordFile() {
		return hasPasswordFile;
	}

	public ConfigurationEntity getConfigurationEntity() {
		return configurationEntity;
	}

	public void setConfigurationEntity(ConfigurationEntity configurationEntity) {
		this.configurationEntity = configurationEntity;
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
