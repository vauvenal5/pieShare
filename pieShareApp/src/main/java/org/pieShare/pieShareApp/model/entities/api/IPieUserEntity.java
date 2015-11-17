/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.entities.api;


/**
 *
 * @author richy
 */
public interface IPieUserEntity extends IBaseEntity{
    	boolean isHasPasswordFile();
	IConfigurationEntity getConfigurationEntity();
	void setConfigurationEntity(IConfigurationEntity configurationEntity);
	void setHasPasswordFile(boolean hasPasswordFile);
	String getUserName();
	void setUserName(String userName);
}
