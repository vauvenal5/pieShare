/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareClient.services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author RicLeo00
 */
public class ClientSendTask implements Runnable {

	private String host = null;
	private int port = -1;
	private DatagramSocket socket;
	private String name;
	private String testMessage = "{\"type\":\"msg\", \"msg\":\"%s\"}";
	private String punchMsg = "{\"type\":\"punch\", \"client\":\"%s\"}";

	public void setName(String name) {
		this.name = name;
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void run() {
		while (true) {

			byte[] msg = String.format(testMessage, "Hello from: " + name).getBytes();
  
			PieLogger.debug(this.getClass(), String.format("%s is attempting to send to host: %s with port: %s", name, host, port));

			send(msg, host, port);
		}
	}

	private boolean send(byte[] msg, String host, int port) {
		try {
			DatagramPacket packet = new DatagramPacket(msg, msg.length, InetAddress.getByName(host), port);
			socket.send(packet);
		}
		catch (UnknownHostException ex) {
			PieLogger.debug(this.getClass(), "UnknownHost .. may be ok, while connecting.", ex);//Logger.getLogger(ClientSendTask.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
		catch (IOException ex) {
			PieLogger.debug(this.getClass(), "IOException while connecting.", ex);//Logger.getLogger(ClientSendTask.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
		return true;
	}

}
