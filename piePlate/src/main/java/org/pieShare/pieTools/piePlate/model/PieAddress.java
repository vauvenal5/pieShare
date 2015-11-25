/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.piePlate.model;


public class PieAddress implements IPieAddress {
	private String cluster;
	private String channelId;
	
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
