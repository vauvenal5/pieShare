/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.model.message.loopHoleMessages;

import org.pieShare.pieTools.piePlate.model.UdpAddress;
import org.pieShare.pieTools.piePlate.model.message.api.IBasePieMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.api.IUdpMessage;

/**
 *
 * @author Richard
 */
public class LoopHoleConnectionMessage extends BaseUdpMessage {

	private String clientPublicIP;
	private String clientPrivateIP;
	private int clientPublicPort;
	private int clientPrivatePort;
	private String fromID;

	public String getClientPublicIP() {
		return clientPublicIP;
	}

	public void setClientPublicIP(String clientPublicIP) {
		this.clientPublicIP = clientPublicIP;
	}

	public String getClientPrivateIP() {
		return clientPrivateIP;
	}

	public void setClientPrivateIP(String clientPrivateIP) {
		this.clientPrivateIP = clientPrivateIP;
	}

	public int getClientPublicPort() {
		return clientPublicPort;
	}

	public void setClientPublicPort(int clientPublicPort) {
		this.clientPublicPort = clientPublicPort;
	}

	public int getClientPrivatePort() {
		return clientPrivatePort;
	}

	public void setClientPrivatePort(int clientPrivatePort) {
		this.clientPrivatePort = clientPrivatePort;
	}

	public String getFromId() {
		return fromID;
	}

	public void setFromId(String fromID) {
		this.fromID = fromID;
	}
}
