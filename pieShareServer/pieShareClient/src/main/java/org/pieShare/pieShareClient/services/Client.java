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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
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
public class Client {

    private final ExecutorService executor;

    public Client() {
        executor = Executors.newCachedThreadPool();
    }

    public void connect(String from, String to) {

        String serverAddress = "172.25.12.98";
        String clientAddress = null;
        int serverPort = 6312;

        String registerMsg = "{\"type\":\"register\", \"name\":\"%s\", \"localAddress\":\"%s\", \"localPort\":\"%s\", \"privateAddress\":\"%s\", \"privatePort\":\"%s\"}";
        String connectMsg = "{\"type\":\"connect\", \"from\":\"%s\", \"to\":\"%s\"}";
        String ackMsg = "{\"type\":\"ACK\", \"from\":\"%s\"}";
        try {

            Socket socket = new Socket(serverAddress, serverPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Send out Register
            String text = String.format(registerMsg, from, socket.getLocalAddress().toString(), socket.getLocalPort(), socket.getInetAddress().toString(), socket.getPort());
            out.println(text);
            out.flush();

            if (to != null) {
                text = String.format(connectMsg, from, to);
                out.println(text);
                out.flush();
            }

            //Send out Connection if available
            //###
            //Wait fot response
            String fromServer = null;
            while ((fromServer = in.readLine()) != null) {
                PieLogger.info(this.getClass(), String.format("Server answere: %s", fromServer));

                JsonObject input = processInput(fromServer);

                if (input.getString("type").equals("connection")) {

                    ClientCommunicationTask task = new ClientCommunicationTask();
                    task.setClientData(input.getJsonObject("client"));
                    task.setLocalClientName(from);
                    executor.execute(task);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JsonObject processInput(String input) {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(input.getBytes());
        JsonReader jsonReader = Json.createReader(byteInStream);
        JsonObject ob = jsonReader.readObject();
        PieLogger.info(this.getClass(), String.format("ConnectionText: %s", ob.toString()));
        return ob;
    }
}
