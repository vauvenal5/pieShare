/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster.discovery.event;

import java.util.EventObject;
import org.pieShare.pieTools.piePlate.model.DiscoveredMember;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class MemberDiscoveredEvent extends EventObject {
	

	public MemberDiscoveredEvent(Object source, DiscoveredMember member) {
		super(source);
	}
	
}
