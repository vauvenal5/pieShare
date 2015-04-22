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
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class NetworkService implements INetworkService {

	private int minPort = 1024;
	private int maxPort = 49151;
	private InetAddress address = null;
        private String nicDisplayName;

        @Override
        public void setNicDisplayName(String nicDisplayName) {
            this.nicDisplayName = nicDisplayName;
        }

	@Override
	public int getAvailablePort() {
		return this.getAvailablePortStartingFrom(this.minPort);
	}
	
	@Override
	public int getAvailablePortStartingFrom(int port) {
		
		for (int p = port; p <= this.maxPort; p++) {
			if(this.checkPort(p))
			{
				return p;
			}
		}
		//todo: throw exception
		return -1;
	}
	
	private boolean checkPort(int p) {
		try {
				ServerSocket tmpSocket = new ServerSocket(p);
				tmpSocket.setReuseAddress(true);
				tmpSocket.close();
				return true;
			} catch (IOException ex) {
				PieLogger.error(this.getClass(), "Find port failed!", ex);
			}
		return false;
	}

	@Override
	public InetAddress getLocalHost() {
		return this.getLocalHost(false);
	}
        
        private boolean useFixedNic() {
            return this.nicDisplayName != null && !this.nicDisplayName.isEmpty();
        }
        
        private boolean checkFixedNic(NetworkInterface nic) {
            if(nic.getDisplayName().equals(this.nicDisplayName)) {
                return true;
            }       
            return false;
        }
        
        private InetAddress checkReachableAddress(InetAddress ad) {
            if(this.useFixedNic()) {
                return ad;
            }
            else {
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
                        PieLogger.info(this.getClass(), "Found internet!");
                        return this.address;
                } catch (IOException ex) {
                        PieLogger.info(this.getClass(), "No internet here!", ex);
                }
            }
            
            return null;
        }
        
        private List<InetAddress> checkAddresses(NetworkInterface nic) {
            List<InetAddress> possibleAds = new ArrayList<>();
            Enumeration<InetAddress> ads = nic.getInetAddresses();

            while (ads.hasMoreElements()) {
                    InetAddress ad = ads.nextElement();
                    try {
                            if (ad instanceof Inet4Address) {
                                    if(ad.isReachable(5000))
                                    {
                                        if(this.checkReachableAddress(ad) == ad) {
                                            possibleAds = new ArrayList<>();
                                            possibleAds.add(ad);
                                            return possibleAds;
                                        }
                                        
                                        possibleAds.add(ad);
                                    }
                            }
                    } catch (IOException ex) {
                            PieLogger.info(this.getClass(), "Well looks bad for internet!", ex);
                    }
            }
            
            return possibleAds;
        }

	@Override
	public InetAddress getLocalHost(boolean invalidate) {
		//todo-sv: try to get local host out of cloud service
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
                                
                                if(this.useFixedNic()) {
                                    if(this.checkFixedNic(nic)) {
                                        List<InetAddress> ads = this.checkAddresses(nic);
                                        if(!ads.isEmpty()) {
                                            return ads.get(0);
                                        }
                                    }
                                }
                                else {
                                    if (!nic.isLoopback() && !nic.isVirtual() && nic.isUp()) {
                                        List<InetAddress> ads = this.checkAddresses(nic);
                                        if(ads.size() == 1) {
                                            return ads.get(0);
                                        }
                                        else {
                                            possibleAds.addAll(ads);
                                        }
                                    }
                                }
			}
		} catch (SocketException ex) {
			PieLogger.info(this.getClass(), "God damit! Give me internet", ex);
		}

		if (possibleAds.size() == 0) {
			//todo: throw exception
			return InetAddress.getLoopbackAddress();
		}
		
		//todo-sv: checkout 0.0.0.0 adress for adressing all adapters

		this.address = possibleAds.get(0);
		return this.address;
	}

	@Override
	public int getNumberOfAvailablePorts(int firstPort, int lastPort) {
		int count = 0;
		
		for(int i = firstPort; i <= lastPort; i++) {
			if(this.checkPort(i)) {
				count++;
			}
		}
		
		return count;
	}
}
