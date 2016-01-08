/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster;

import org.pieShare.pieTools.piePlate.model.DiscoveredMember;


public interface IEndpointCallback {
	public void NonRespondingEndpoint(DiscoveredMember member);
}
