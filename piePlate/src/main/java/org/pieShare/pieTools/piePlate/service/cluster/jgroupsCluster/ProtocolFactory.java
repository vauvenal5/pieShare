/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.JChannel;
import static org.jgroups.Message.RSVP;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.protocols.BARRIER;
import org.jgroups.protocols.FD_ALL;
import org.jgroups.protocols.FD_SOCK;
import org.jgroups.protocols.FORWARD_TO_COORD;
import org.jgroups.protocols.FRAG2;
import org.jgroups.protocols.MERGE3;
import org.jgroups.protocols.MFC;
import org.jgroups.protocols.PING;
import org.jgroups.protocols.RSVP;
import org.jgroups.protocols.UFC;
import org.jgroups.protocols.UNICAST3;
import org.jgroups.protocols.VERIFY_SUSPECT;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.protocols.relay.RELAY2;
import org.jgroups.protocols.relay.config.RelayConfig;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.util.UUID;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.protocols.LoopHoleDiscovery;
import org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.protocols.UDP;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;


/**
 *
 * @author Svetoslav
 */
public class ProtocolFactory {
	
	private IBeanService beanService;

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}
	
	public ProtocolFactory() {
		ClassConfigurator.addProtocol((short)776, UDP.class);
		ClassConfigurator.addProtocol((short)777, LoopHoleDiscovery.class);
	}
	
	public Protocol[] getBridgeConfig() {
		UDP udp = new UDP();
		udp.setIpMcast(false);
		udp.setBindPort(1234);
		
		LoopHoleDiscovery lhd = this.beanService.getBean(LoopHoleDiscovery.class);
		
		Protocol[] protStack={
			udp,
			lhd,
			new MERGE3(),
			new FD_SOCK(),
			new FD_ALL(),
			new VERIFY_SUSPECT(),
			new BARRIER(),
			new NAKACK2(),
			new UNICAST3(),
			new STABLE(),
			new GMS(),
			new UFC(),
			new MFC(),
			new FRAG2(),
			new RSVP(),
			new STATE_TRANSFER()
		};
		
		return protStack;
	}
	
	public Protocol[] getUdpStack() {
		
		RelayConfig.ProgrammaticBridgeConfig config = 
				new RelayConfig.ProgrammaticBridgeConfig("global", this.getBridgeConfig());
		
		RelayConfig.SiteConfig site1 = new RelayConfig.SiteConfig("test1");
		site1.addBridge(config);
		
		RelayConfig.SiteConfig site2 = new RelayConfig.SiteConfig("test2");
		site2.addBridge(config);
		
		//todo: dynamic adding of sites when they join?
		RELAY2 relay2 = new RELAY2();
		relay2.site("test1");
		relay2.relayMulticasts(true);
		relay2.addSite("test1", site1);
		relay2.addSite("test2", site2);
		
		Protocol[] protStack={
			new UDP(),
			new PING(),
			new MERGE3(),
			new FD_SOCK(),
			new FD_ALL(),
			new VERIFY_SUSPECT(),
			new BARRIER(),
			new NAKACK2(),
			new UNICAST3(),
			new STABLE(),
			new GMS(),
			new UFC(),
			new MFC(),
			new FRAG2(),
			new RSVP(),
			new STATE_TRANSFER(),
			relay2,
			new FORWARD_TO_COORD()
		};
		
		return protStack;
	}
	
}
