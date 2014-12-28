/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.pieShare.pieShareServer.model.UserData;
import org.pieShare.pieShareServer.services.api.IIncomeTask;
import org.pieShare.pieShareServer.services.api.IServer;
import org.pieShare.pieShareServer.services.api.IUserPersistenceService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class IncomeTask implements IIncomeTask {

	private Socket clientSocket;
	private IServer server;
	private IUserPersistenceService userPersistenceService;
	private PrintWriter out;

	public void setUserPersistenceService(IUserPersistenceService userPersistenceService) {
		this.userPersistenceService = userPersistenceService;
	}

	public void setServer(IServer server) {
		this.server = server;
	}

	@Override
	public void setSocket(Socket socket) {
		this.clientSocket = socket;
	}

	@Override
	public void run() {

		String connectionMsg = "{\"type\":\"connection\", \"client\":%s}";
		PieLogger.info(this.getClass(), "New Connection.");
/*
		try {

			out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				JsonObject input = processInput(inputLine);
				if (input.getString("type").equals("register")) {
					UserData data = new UserData();
					data.setConnectionInfo(input);
					data.setTask(this);
					userPersistenceService.addUser(input.getString("name"), data);
					PieLogger.info(this.getClass(), String.format("User: %s successful registered.", input.getString("name")));
				}
				else if (input.getString("type").equals("connect")) {
					UserData from = userPersistenceService.getUser(input.getString("from"));
					UserData to = userPersistenceService.getUser(input.getString("to"));

					to.getTask().sendData(String.format(connectionMsg, from.getConnectionInfo().toString()));
					from.getTask().sendData(String.format(connectionMsg, to.getConnectionInfo().toString()));
				}
			}
		}
		catch (IOException ex) {
			PieLogger.error(this.getClass(), "Error in IncomeTask.", ex);
		}*/
	}

	@Override
	public void sendData(String data) {
		out.println(data);
	}

	public JsonObject processInput(String input) {
		ByteArrayInputStream byteInStream = new ByteArrayInputStream(input.getBytes());
		JsonReader jsonReader = Json.createReader(byteInStream);
		JsonObject ob = jsonReader.readObject();
		PieLogger.info(this.getClass(), String.format("ConnectionText: %s", ob.toString()));
		return ob;
	}

}
