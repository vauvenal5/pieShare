/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket;

import java.net.InetAddress;

public class ZeroMQUtilsService {

	public String buildConnectionString(InetAddress address, int port) {
		return this.buildConnectionString(address.getHostAddress(), port);
	}

	public String buildConnectionString(String address, int port) {
		String connectionString = String.format("tcp://%s:%s", address, port);
		return connectionString;
	}
}
