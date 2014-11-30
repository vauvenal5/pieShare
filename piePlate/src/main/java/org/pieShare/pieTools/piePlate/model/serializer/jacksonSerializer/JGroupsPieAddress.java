/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.model.serializer.jacksonSerializer;

import org.jgroups.Address;
import org.pieShare.pieTools.piePlate.model.IPieAddress;

/**
 *
 * @author Svetoslav
 */
public class JGroupsPieAddress implements IPieAddress {

	private Address address;
	private String cluster;
	private String channelId;

	public void setAddress(Address address) {
		this.address = address;
	}

	public Address getAddress() {
		return this.address;
	}

	@Override
	public String getClusterName() {
		return this.cluster;
	}

	@Override
	public void setClusterName(String cluster) {
		this.cluster = cluster;
	}

	@Override
	public String getChannelId() {
		return this.channelId;
	}

	@Override
	public void setChannelId(String id) {
		this.channelId = id;
	}
}
