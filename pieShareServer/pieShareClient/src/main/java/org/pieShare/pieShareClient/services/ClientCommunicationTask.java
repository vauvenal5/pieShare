/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareClient.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class ClientCommunicationTask implements Runnable {

	private JsonObject clientData;
	private String localClientName;

	public void setClientData(JsonObject data) {
		clientData = data;
	}

	public void setLocalClientName(String localClientName) {
		this.localClientName = localClientName;
	}

	public void run() {

		String punchMsg = "{\"type\":\"punch\", \"from\":\"%s\", \"to\":\"%s\"}";

		boolean found = false;
		Socket socketToClient = null;

		PrintWriter out = null;
		BufferedReader in = null;

		while (!found) {
			try {
				socketToClient = new Socket(clientData.getString("localAddress"), clientData.getInt("localPort"));
				socketToClient.setSoTimeout(3000);
				out = new PrintWriter(socketToClient.getOutputStream(), true);

				String msg = String.format(punchMsg, localClientName, clientData.getString("name"));
				PieLogger.info(this.getClass(), String.format("WriteToClient: %s", msg));
				out.write(msg);

				in = new BufferedReader(new InputStreamReader(socketToClient.getInputStream()));

				found = WaitForACKPunch(in, out);

			}
			catch (IOException ex) {
				Logger.getLogger(ClientCommunicationTask.class.getName()).log(Level.SEVERE, null, ex);
			}

			if (!found) {
				try {
					socketToClient = new Socket(clientData.getString("privateAddress"), clientData.getInt("privatePort"));
					socketToClient.setSoTimeout(3000);
					out = new PrintWriter(socketToClient.getOutputStream(), true);

					String msg = String.format(punchMsg, localClientName, clientData.getString("name"));
					PieLogger.info(this.getClass(), String.format("WriteToClient: %s", msg));
					out.write(msg);

					in = new BufferedReader(new InputStreamReader(socketToClient.getInputStream()));

					found = WaitForACKPunch(in, out);

				}
				catch (IOException ex) {
					Logger.getLogger(ClientCommunicationTask.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}

		try {
			socketToClient.setSoTimeout(0);

			PieLogger.info(this.getClass(), "ACK Recieved");
			out.write("Hello From" + localClientName);

			String fromServer;

			while ((fromServer = in.readLine()) != null) {
				PieLogger.info(this.getClass(), "Received: " + fromServer);
			}
		}
		catch (SocketException ex) {
			Logger.getLogger(ClientCommunicationTask.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (IOException ex) {
			Logger.getLogger(ClientCommunicationTask.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public boolean WaitForACKPunch(BufferedReader reader, PrintWriter out) {

		String ackMsg = "{\"type\":\"ACK\"}";
		try {
			String fromServer;

			while ((fromServer = reader.readLine()) != null) {

				JsonObject input = processInput(fromServer);
				if (input.getString("type").equals("ACK")) {
					return true;
				}
				else if (input.getString("type").equals("ACK")) {
					out.write(ackMsg);
				}
			}
		}
		catch (Exception ex) {
			Logger.getLogger(ClientCommunicationTask.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	public JsonObject processInput(String input) {
		ByteArrayInputStream byteInStream = new ByteArrayInputStream(input.getBytes());
		JsonReader jsonReader = Json.createReader(byteInStream);
		JsonObject ob = jsonReader.readObject();
		PieLogger.info(this.getClass(), String.format("ConnectionText: %s", ob.toString()));
		return ob;
	}

}
