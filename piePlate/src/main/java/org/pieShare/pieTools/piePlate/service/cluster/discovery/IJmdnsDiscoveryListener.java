/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.discovery;

import javax.jmdns.ServiceListener;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.IMemberDiscoveredListener;
import org.pieShare.pieTools.piePlate.service.cluster.discovery.event.MemberDiscoveredEvent;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public interface IJmdnsDiscoveryListener extends ServiceListener{
	void setDiscoveryService(ZeroconfigDiscoveryService discoveryService);
	void setMyself(String myself);
	void setCloudName(String cloudName);
	IEventBase<IMemberDiscoveredListener, MemberDiscoveredEvent> getMemberDiscoveredEventBase();
}
