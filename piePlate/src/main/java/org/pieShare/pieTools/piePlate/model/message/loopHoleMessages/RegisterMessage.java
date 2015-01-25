/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.model.message.loopHoleMessages;

import org.pieShare.pieTools.piePlate.model.message.UdpMessage;

/**
 *
 * @author Richard
 */
public class RegisterMessage extends UdpMessage {

	private String privateHost;
	private int privatePort;
	private String name;

	public String getPrivateHost() {
		return privateHost;
	}

	public void setPrivateHost(String privateHost) {
		this.privateHost = privateHost;
	}

	public int getPrivatePort() {
		return privatePort;
	}

	public void setPrivatePort(int privatePort) {
		this.privatePort = privatePort;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
