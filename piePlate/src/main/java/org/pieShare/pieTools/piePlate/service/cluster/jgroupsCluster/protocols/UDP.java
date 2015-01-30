/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.protocols;

/**
 *
 * @author Svetoslav
 */
public class UDP extends org.jgroups.protocols.UDP {
	public void setIpMcast(boolean ipMcast) {
		this.ip_mcast = ipMcast;
	}
}
