/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareClient.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.bouncycastle.util.Arrays;
import org.pieShare.pieShareClient.api.Callback;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author RicLeo00
 */
public class ClientTask implements Runnable {

	private DatagramSocket socket;
	private Callback callback;
	private ClientSendTask sendTask;
	private final ExecutorService executor;

	public ClientTask() {
		executor = Executors.newCachedThreadPool();
	}

	public void setSendTask(ClientSendTask sendTask) {
		this.sendTask = sendTask;
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public void run() {
		while (true) {
			byte[] bytes = new byte[1024];
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

			try {
				socket.receive(packet);
				InputTask task = new InputTask();
				task.setPacket(packet);
				task.setSendTask(sendTask);
				task.setCallback(callback);
				executor.execute(task);
			}
			catch (IOException ex) {
				PieLogger.debug(this.getClass(), "Error receive:", ex);
			}
		}
	}

}
