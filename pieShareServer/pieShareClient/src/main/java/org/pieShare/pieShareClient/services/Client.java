/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareClient.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.pieShare.pieShareClient.api.Callback;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class Client {

    private DatagramSocket socket;
    private final ExecutorService executor;

    public Client() {
        try {
            socket = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        executor = Executors.newCachedThreadPool();
    }

    public void connect(String from, String to) {

        String serverAddress = "192.168.0.22";
        int serverPort = 6312;

        String registerMsg = "{\"type\":\"register\", \"name\":\"%s\", \"localAddress\":\"%s\", \"localPort\":%s, \"privateAddress\":\"%s\", \"privatePort\":%s}";
        String connectMsg = "{\"type\":\"connect\", \"from\":\"%s\", \"to\":\"%s\"}";
        String ackMsg = "{\"type\":\"ACK\", \"from\":\"%s\"}";

        ClientTask task = new ClientTask();
        task.setCallback(new Callback() {

            public void Handle(JsonObject client) {
                ClientSendTask sendTask = new ClientSendTask();
                sendTask.setHost(client.getString("privateHost"));
                sendTask.setPort(client.getInt("privatePort"));
                sendTask.setSocket(socket);
                executor.execute(sendTask);
            }
        });

        try {
            //task.setSocket(socket);

           // Socket test = new Socket("www.google.com", 80);
            
            //executor.execute(task);
            DatagramPacket packet = new DatagramPacket("temp".getBytes(), 4, InetAddress.getByName(serverAddress), serverPort);
            String text = String.format(registerMsg, from, "temp", -1, packet.getAddress(), packet.getPort());
            socket.send(packet);
          
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

         //Send out Register
        // text = String.format(registerMsg, from, socket.getLocalAddress().toString().replace("/", ""), socket.getLocalPort(), socket.getInetAddress().toString().replace("/", ""), socket.getPort());
    }

    public JsonObject processInput(String input) {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(input.getBytes());
        JsonReader jsonReader = Json.createReader(byteInStream);
        JsonObject ob = jsonReader.readObject();
        PieLogger.info(this.getClass(), String.format("ConnectionText: %s", ob.toString()));
        return ob;
    }

}
