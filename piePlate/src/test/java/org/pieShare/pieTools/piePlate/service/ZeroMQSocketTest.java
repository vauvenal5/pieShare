/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Before;
import org.junit.Test;
import org.pieShare.pieTools.piePlate.model.DiscoveredMember;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.IEndpointCallback;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.PieDealer;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.PieRouter;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.ZeroMQUtilsService;

public class ZeroMQSocketTest {
	
	protected class CallbackMock implements IEndpointCallback{
		@Override
		public void NonRespondingEndpoint(DiscoveredMember member) {
		}
	}
	
	private void addMember(ArrayList<DiscoveredMember> members, int port){
		try {
			InetAddress routerAddress = InetAddress.getLocalHost();
			DiscoveredMember local = new DiscoveredMember();
			local.setInetAdresses(routerAddress);
			local.setPort(port);
			local.setName("member");
			members.add(local);
			//return members;
		} catch (Exception e){
		}
		
		//return members;
	} 

	@Test
	public void testSendRecv() {
		Thread n = new Thread(new Runnable() {
			@Override
			public void run() {
				PieDealer dealer = new PieDealer();
				dealer.setZeroMQUtilsService(new ZeroMQUtilsService());
				
				byte[] messageSend = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
				
				ArrayList<DiscoveredMember> members = new ArrayList<>();
				addMember(members, 9000);

				dealer.send(members, messageSend, new CallbackMock());
			}
		});

		byte[] messageSend = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		byte[] messageRecv = null;

		PieRouter router = new PieRouter();
		router.setZeroMQUtilsService(new ZeroMQUtilsService());

		router.bind(9000);

		n.start();

		messageRecv = router.receive();

		router.close();

		assertArrayEquals(messageSend, messageRecv);
	}

	@Test
	public void testMultipleSenders() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				PieDealer dealer = new PieDealer();
				dealer.setZeroMQUtilsService(new ZeroMQUtilsService());								
				byte[] messageSend = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
				
				ArrayList<DiscoveredMember> members = new ArrayList<>();
				addMember(members, 9000);
				
				dealer.send(members, messageSend, new CallbackMock());
			}
		};

		byte[] messageSend = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		byte[] messageRecv = null;

		PieRouter router = new PieRouter();
		router.setZeroMQUtilsService(new ZeroMQUtilsService());

		router.bind(9000);

		for (int i = 0; i < 5; i++) {
			Thread n = new Thread(r);
			n.start();
		}

		for (int i = 0; i < 5; i++) {
			messageRecv = router.receive();
			assertArrayEquals(messageSend, messageRecv);
		}

		router.close();
	}

	@Test
	public void testSendMultipleRecv() {
		Thread n = new Thread(new Runnable() {
			@Override
			public void run() {
				PieDealer dealer = new PieDealer();
				dealer.setZeroMQUtilsService(new ZeroMQUtilsService());
				
				byte[] messageSend = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
				
				ArrayList<DiscoveredMember> members = new ArrayList<>();
				addMember(members, 9000);
				addMember(members, 9001);
				
				dealer.send(members, messageSend, new CallbackMock());
			}
		});

		byte[] messageSend = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		byte[] messageRecv = null;

		PieRouter router = new PieRouter();
		router.setZeroMQUtilsService(new ZeroMQUtilsService());
		PieRouter router2 = new PieRouter();
		router2.setZeroMQUtilsService(new ZeroMQUtilsService());

		router.bind(9000);
		router2.bind(9001);

		n.start();

		messageRecv = router.receive();
		assertArrayEquals(messageSend, messageRecv);

		messageRecv = router2.receive();
		assertArrayEquals(messageSend, messageRecv);

		router.close();
		router2.close();
	}

}
