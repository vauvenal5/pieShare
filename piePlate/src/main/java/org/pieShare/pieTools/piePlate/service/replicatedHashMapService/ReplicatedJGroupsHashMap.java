/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.piePlate.service.replicatedHashMapService;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.jgroups.JChannel;
import org.jgroups.blocks.ReplicatedHashMap;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.JGroupsClusterService;

/**
 *
 * @author Svetoslav
 */
public class ReplicatedJGroupsHashMap<K,V> implements IReplicatedHashMap<K,V>{
	//TODO-urgent: check this out!!!
	private ReplicatedHashMap<K, V> map;
	private JChannel channel;

	@PostConstruct
	public void postReplicatedJGroupsHashMap() {
		map = new ReplicatedHashMap<>(this.channel);
	}
	
	public void setChannel(JChannel channel) {
		this.channel = channel;
	}

	@Override
	public Map<K, V> getMap() {
		return this.map;
	}

}
