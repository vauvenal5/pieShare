/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author Richy
 */
@Entity
public class PieUserEntity implements Serializable {

	@Id
	private String userName;

	private boolean hasPasswordFile;

	public boolean isHasPasswordFile() {
		return hasPasswordFile;
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
