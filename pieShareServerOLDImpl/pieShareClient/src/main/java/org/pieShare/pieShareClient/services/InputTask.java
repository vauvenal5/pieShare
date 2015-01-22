/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareClient.services;

import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.bouncycastle.util.Arrays;
import org.pieShare.pieShareClient.api.Callback;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class InputTask implements Runnable {

	private DatagramPacket packet;
	private ClientSendTask sendTask;
	private String ackMsg = "{\"type\":\"ACK\"}";
	private Callback callback;

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public void setSendTask(ClientSendTask sendTask) {
		this.sendTask = sendTask;
	}

	public void setPacket(DatagramPacket packet) {
		this.packet = packet;
	}

	public void run() {
		newPacketReceived();
	}

	private synchronized void newPacketReceived() {
		byte[] bytes = new byte[1024];
		bytes = Arrays.copyOfRange(bytes, 0, packet.getLength());
		PieLogger.info(this.getClass(), String.format("Input Message: %s", new String(bytes)));
		JsonObject input = processInput(bytes);

		if (input.getString("type").equals("connection")) {
			JsonObject newClient = input.getJsonObject("client");
			callback.Handle(newClient);
		}
		if (input.getString("type").equals("msg")) {
			System.out.println("Message Arrived: " + input.getString("msg"));
		}
		if (input.getString("type").equals("punch")) {
			sendTask.send(ackMsg.getBytes(), packet.getAddress().getHostAddress(), packet.getPort());
		}
		if (input.getString("type").equals("ACK")) {
			sendTask.setACK(true);
		}
	}

	public JsonObject processInput(byte[] input) {
		ByteArrayInputStream byteInStream = new ByteArrayInputStream(input);
		JsonReader jsonReader = Json.createReader(byteInStream);
		JsonObject ob = jsonReader.readObject();
		PieLogger.info(this.getClass(), String.format("ConnectionText: %s", ob.toString()));
		return ob;
	}
}
