/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.networkService;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Svetoslav
 */
public class NetworkService implements INetworkService {

	private int minPort = 1024;
	private int maxPort = 49151;
	private InetAddress address = null;

	@Override
	public int getAvailablePort() {
		return this.getAvailablePortStartingFrom(this.minPort);
	}
	
	@Override
	public int getAvailablePortStartingFrom(int port) {
		
		for (int p = port; p <= this.maxPort; p++) {
			try {
				ServerSocket tmpSocket = new ServerSocket(p);
				tmpSocket.setReuseAddress(true);
				tmpSocket.close();
				return p;
			} catch (IOException ex) {
				Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		//todo: throw exception
		return -1;
	}

	@Override
	public InetAddress getLocalHost() {
		return this.getLocalHost(false);
	}

	@Override
	public InetAddress getLocalHost(boolean invalidate) {

		if (invalidate) {
			this.address = null;
		}

		if (this.address != null) {
			return this.address;
		}

		List<InetAddress> possibleAds = new ArrayList<>();

		try {
			Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
			while (nics.hasMoreElements()) {
				NetworkInterface nic = nics.nextElement();
				
				if (!nic.isLoopback() && !nic.isVirtual() && nic.isUp()) {
					Enumeration<InetAddress> ads = nic.getInetAddresses();

					while (ads.hasMoreElements()) {
						InetAddress ad = ads.nextElement();
						try {
							if (ad instanceof Inet4Address) {
								if(ad.isReachable(5000))
								{
									possibleAds.add(ad);

									//test internet connection
									try (SocketChannel socket = SocketChannel.open()) {
										socket.socket().setSoTimeout(5000);

										int freePort = this.getAvailablePort();

										socket.bind(new InetSocketAddress(ad, freePort));
										//this has to become way better
										//socket.connect(new InetSocketAddress("google.com", 80));
										//if everything passes the InetAddress should be okay.
										socket.close();
										this.address = ad;
										return this.address;
									} catch (IOException ex) {
										Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
									}
								}
							}
						} catch (IOException ex) {
							Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
			}
		} catch (SocketException ex) {
			Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
		}

		if (possibleAds.size() == 0) {
			//todo: throw exception
			return InetAddress.getLoopbackAddress();
		}
		
		//todo-sv: checkout 0.0.0.0 adress for adressing all adapters

		this.address = possibleAds.get(0);
		return this.address;
	}
}
