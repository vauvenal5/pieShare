/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Provider;
import org.jgroups.util.MockTimeScheduler;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.pieShare.pieTools.piePlate.model.DiscoveredMember;
import org.pieShare.pieTools.piePlate.service.channel.api.IIncomingChannel;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.IEndpointCallback;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.PieDealer;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.PieRouter;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.ZeroMQUtilsService;
import org.pieShare.pieTools.piePlate.task.ChannelTask;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

public class ZeroMQSocketTest {
	
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
	
	private void waitForThread(Thread t) throws Exception {
		while(t.isAlive()) {
			Thread.sleep(500);
		}
	}
	
	private Thread getRouterThread(PieRouter router, int routerPort, final ChannelTask task) {
		router.setZeroMQUtilsService(new ZeroMQUtilsService());
		router.setPort(routerPort);
		
		IExecutorService executor = Mockito.mock(IExecutorService.class);
		router.setExecutorService(executor);
		
		router.setChannelTaskProvider(new Provider<ChannelTask>() {
			@Override
			public ChannelTask get() {
				return task;
			}
		});
		
		router.registerIncomingChannel(Mockito.mock(IIncomingChannel.class));

		return new Thread(router);
	}

	@Test
	public void testSendRecv() throws Exception {
		PieLogger.info(this.getClass(), "Starting testSendRecv!");
		final byte[] messageSend = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		final int routerPort = 9000;
		Thread n = new Thread(new Runnable() {
			@Override
			public void run() {
				PieDealer dealer = new PieDealer();
				dealer.setZeroMQUtilsService(new ZeroMQUtilsService());
				
				ArrayList<DiscoveredMember> members = new ArrayList<>();
				addMember(members, routerPort);

				dealer.send(members, messageSend, Mockito.mock(IEndpointCallback.class));
			}
		});

		
		byte[] messageRecv = null;

		PieRouter router = new PieRouter();
		ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
		ChannelTask task = Mockito.mock(ChannelTask.class);
		Thread routerThread = this.getRouterThread(router, routerPort, task);
		
		routerThread.start();
		n.start();
		
		this.waitForThread(n);

		Mockito.verify(task).setMessage(captor.capture());
		messageRecv = captor.getValue();

		router.close();
		
		this.waitForThread(routerThread);

		assertArrayEquals(messageSend, messageRecv);
	}

	@Test
	public void testMultipleSenders() throws Exception {
		PieLogger.info(this.getClass(), "Starting testMultipleSenders!");
		final byte[] messageSend = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		final int routerPort = 9000;
		Runnable r = new Runnable() {
			@Override
			public void run() {
				PieDealer dealer = new PieDealer();
				dealer.setZeroMQUtilsService(new ZeroMQUtilsService());								
				
				ArrayList<DiscoveredMember> members = new ArrayList<>();
				addMember(members, routerPort);
				
				dealer.send(members, messageSend, Mockito.mock(IEndpointCallback.class));
			}
		};

		PieRouter router = new PieRouter();
		ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
		ChannelTask task = Mockito.mock(ChannelTask.class);
		Thread routerThread = this.getRouterThread(router, routerPort, task);
		
		routerThread.start();
		
		Thread[] threads = new Thread[5];

		for (int i = 0; i < 5; i++) {
			threads[i] = new Thread(r);
			threads[i].start();
		}
		
		for(int i = 0; i<5;i++) {
			this.waitForThread(threads[i]);
		}
		
		Mockito.verify(task, Mockito.times(5)).setMessage(captor.capture());
		List<byte[]> res = captor.getAllValues();
		
		router.close();
		
		this.waitForThread(routerThread);

		for (int i = 0; i < 5; i++) {
			assertArrayEquals(messageSend, res.get(i));
		}
	}

	@Test
	public void testSendMultipleRecv() throws Exception {
		PieLogger.info(this.getClass(), "Starting testSendMultipleRecv!");
		final byte[] messageSend = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		final int routerPort = 9000;
		final int routerPort2 = 9001;
		Thread n = new Thread(new Runnable() {
			@Override
			public void run() {
				PieDealer dealer = new PieDealer();
				dealer.setZeroMQUtilsService(new ZeroMQUtilsService());
				
				ArrayList<DiscoveredMember> members = new ArrayList<>();
				addMember(members, routerPort);
				addMember(members, routerPort2);
				
				dealer.send(members, messageSend, Mockito.mock(IEndpointCallback.class));
			}
		});
		
		PieRouter router = new PieRouter();
		ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
		ChannelTask task = Mockito.mock(ChannelTask.class);
		Thread routerThread = this.getRouterThread(router, routerPort, task);
		
		PieRouter router2 = new PieRouter();
		ChannelTask task2 = Mockito.mock(ChannelTask.class);
		Thread routerThread2 = this.getRouterThread(router2, routerPort2, task2);

		routerThread.start();
		routerThread2.start();

		n.start();
		
		this.waitForThread(n);

		router.close();
		router2.close();
		
		this.waitForThread(routerThread);
		this.waitForThread(routerThread2);
		
		Mockito.verify(task).setMessage(captor.capture());
		assertArrayEquals(messageSend, captor.getValue());
		
		Mockito.verify(task2).setMessage(captor.capture());
		assertArrayEquals(messageSend, captor.getValue());
	}

}
