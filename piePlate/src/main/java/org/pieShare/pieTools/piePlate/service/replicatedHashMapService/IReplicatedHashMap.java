/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.piePlate.service.replicatedHashMapService;

import java.util.Map;

/**
 *
 * @author Svetoslav
 */
public interface IReplicatedHashMap<K,V> {
	Map<K,V> getMap();
}
