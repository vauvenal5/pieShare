/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.discovery;

import java.util.List;
import org.pieShare.pieTools.piePlate.model.DiscoveredMember;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.IMemberDiscoveredListener;
import org.pieShare.pieTools.pieUtilities.service.shutDownService.api.IShutdownableService;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public interface IDiscoveryService extends IShutdownableService {
	void registerService(String clusterName, int port) throws DiscoveryException;
	List<DiscoveredMember> list() throws DiscoveryException;
	void addMemberDiscoveredListener(IMemberDiscoveredListener listener);
	void removeMemberDiscoveredListener(IMemberDiscoveredListener listener);
}
