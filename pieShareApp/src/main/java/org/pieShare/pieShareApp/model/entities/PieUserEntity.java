/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author Richy
 */
@Entity
public class PieUserEntity implements Serializable {

	private byte[] password;
	@Id
	private String userName;

	public  byte[] getPassword() {
		return password;
	}

	public void setPassword( byte[] password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
