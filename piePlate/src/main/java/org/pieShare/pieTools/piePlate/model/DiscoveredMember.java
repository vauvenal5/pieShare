/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.model;

import java.net.InetAddress;
import java.util.EventObject;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class DiscoveredMember {
	private InetAddress inetAdresses;
	private int port;

	public InetAddress getInetAdresses() {
		return inetAdresses;
	}

	public void setInetAdresses(InetAddress inetAdresses) {
		this.inetAdresses = inetAdresses;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
