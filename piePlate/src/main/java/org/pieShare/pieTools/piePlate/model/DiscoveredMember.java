/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.model;

import java.net.InetAddress;
import java.util.Objects;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class DiscoveredMember {
	private InetAddress inetAdresses;
	private int port;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 71 * hash + Objects.hashCode(this.inetAdresses);
		hash = 71 * hash + this.port;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DiscoveredMember other = (DiscoveredMember) obj;
		if (this.port != other.port) {
			return false;
		}
		if (Objects.equals(this.inetAdresses, other.inetAdresses)) {
			return true;
		}
		return false;
	}
}
