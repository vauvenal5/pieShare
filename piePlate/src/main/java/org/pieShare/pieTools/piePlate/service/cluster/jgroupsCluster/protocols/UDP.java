/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster.protocols;

import java.net.DatagramSocket;
import java.net.InetAddress;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class UDP extends org.jgroups.protocols.UDP {
	public void setIpMcast(boolean ipMcast) {
		this.ip_mcast = ipMcast;
	}
	
	@Override
	protected void _send(InetAddress dest, int port, boolean mcast, byte[] data, int offset, int length) throws Exception {
		int lp = sock.getLocalPort();
		int p = sock.getPort();
		/*if(p == -1 && port == 1234) {
			sock = new DatagramSocket(1234);
			p = sock.getPort();
		}*/
		PieLogger.debug(this.getClass(), "Socket local port: {} Socket port: {} Dest: {} Des port: {}", lp, p, dest, port);
		super._send(dest, port, mcast, data, offset, length);
	}
}
