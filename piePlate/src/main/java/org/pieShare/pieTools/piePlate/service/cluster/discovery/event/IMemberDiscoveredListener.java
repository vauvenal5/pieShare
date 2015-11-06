/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.discovery.event;

import org.pieShare.pieTools.piePlate.model.DiscoveredMember;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventListener;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public interface IMemberDiscoveredListener extends IEventListener<MemberDiscoveredEvent> {
	
}
