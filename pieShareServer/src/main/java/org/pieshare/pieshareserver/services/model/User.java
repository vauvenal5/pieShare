/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.pieshareserver.services.model;

import org.pieShare.pieTools.piePlate.model.UdpAddress;

/**
 *
 * @author Richard
 */
public class User {
	private String name;
	private String id;
	private UdpAddress privateAddress;
	private UdpAddress publicAddress;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UdpAddress getPrivateAddress() {
		return privateAddress;
	}

	public void setPrivateAddress(UdpAddress privateAddress) {
		this.privateAddress = privateAddress;
	}

	public UdpAddress getPublicAddress() {
		return publicAddress;
	}

	public void setPublicAddress(UdpAddress publicAddress) {
		this.publicAddress = publicAddress;
	}
	
}
