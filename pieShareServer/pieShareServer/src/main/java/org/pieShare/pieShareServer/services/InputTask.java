/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.pieShare.pieShareServer.model.UserData;
import org.pieShare.pieShareServer.services.api.IUserPersistenceService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author RicLeo00
 */
public class InputTask implements Runnable {

    private IUserPersistenceService userPersistenceService;
    private DatagramSocket ds;
    private String connectionMsg = "{\"type\":\"connection\", \"client\":%s}";
    private String registerMsg = "{\"type\":\"connectionInfo\", \"name\":\"%s\", \"localAddress\":\"%s\", \"localPort\":%s, \"privateAddress\":\"%s\", \"privatePort\":%s}";
    private UserData data;

    public void setUserPersistenceService(IUserPersistenceService userPersistenceService) {
        this.userPersistenceService = userPersistenceService;
    }

    public void run() {
        try {
            System.out.println("Server waiting");
            byte[] bytes = new byte[1024];
            ds = new DatagramSocket(6312);
            while (true) {
                DatagramPacket p = new DatagramPacket(bytes, bytes.length);
                ds.receive(p);
                JsonObject input = processInput(p.getData());
                if (input.getString("type").equals("register")) {
                    data = new UserData();
                    String connectionInfo = String.format(registerMsg, input.getString("name"), p.getAddress().toString(), p.getPort(), p.getAddress().toString(), p.getPort());
                    data.setConnectionInfo(processInput(connectionInfo.getBytes()));
                    data.setTask(this);
                    userPersistenceService.addUser(input.getString("name"), data);
                    PieLogger.info(this.getClass(), String.format("User: %s successful registered.", input.getString("name")));
                } else if (input.getString("type").equals("connect")) {
                    UserData from = userPersistenceService.getUser(input.getString("from"));
                    UserData to = userPersistenceService.getUser(input.getString("to"));

                    to.getTask().sendData(String.format(connectionMsg, from.getConnectionInfo().toString()));
                    from.getTask().sendData(String.format(connectionMsg, to.getConnectionInfo().toString()));
                }
            }
           // ds.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JsonObject processInput(byte[] input) {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(input);
        JsonReader jsonReader = Json.createReader(byteInStream);
        JsonObject ob = jsonReader.readObject();
        PieLogger.info(this.getClass(), String.format("ConnectionText: %s", ob.toString()));
        return ob;
    }

    public void sendData(String msg){//), String host, int port) {
        byte[] bA;
        bA = msg.getBytes();

        DatagramPacket pck;
        try {
            pck = new DatagramPacket(bA, bA.length, InetAddress.getByName(data.getConnectionInfo().getString("localAddress")), data.getConnectionInfo().getInt("localPort"));
            ds.send(pck);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
