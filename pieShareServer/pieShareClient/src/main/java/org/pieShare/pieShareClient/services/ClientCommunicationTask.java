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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
public class ClientCommunicationTask implements Runnable {

    private JsonObject clientData;
    private String localClientName;
    private boolean found = false;

    private final ExecutorService executor;

    public ClientCommunicationTask() {
        executor = Executors.newCachedThreadPool();
    }

    public void setClientData(JsonObject data) {
        clientData = data;
    }

    public void setLocalClientName(String localClientName) {
        this.localClientName = localClientName;
    }


    public void run() {

      /*  String punchMsg = "{\"type\":\"punch\", \"from\":\"%s\", \"to\":\"%s\"}";

        Socket socketToClient = null;

        PrintWriter out = null;
        BufferedReader in = null;

        while(true)
        {
        try {
            socketToClient = new Socket(clientData.getString("localAddress"), clientData.getInt("localPort"));
            socketToClient.setSoTimeout(3000);
            out = new PrintWriter(socketToClient.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socketToClient.getInputStream()));

            Puncher puncher = new Puncher();
            puncher.setOut(out);
            puncher.setReader(in);


            puncher.setBack(back);

            executor.execute(puncher);

            String msg = String.format(punchMsg, localClientName, clientData.getString("name"));

            while (!found) {
                PieLogger.info(this.getClass(), String.format("WriteToClient: %s", msg));
                out.println(msg);
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientCommunicationTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        }

        if (!found) {
            try {
                socketToClient = new Socket(clientData.getString("privateAddress"), clientData.getInt("privatePort"));

                socketToClient.setSoTimeout(3000);
                out = new PrintWriter(socketToClient.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socketToClient.getInputStream()));

                Puncher puncher = new Puncher();
                puncher.setOut(out);
                puncher.setReader(in);

               

                puncher.setBack(back);

                executor.execute(puncher);

                String msg = String.format(punchMsg, localClientName, clientData.getString("name"));

                while (!found) {
                    PieLogger.info(this.getClass(), String.format("WriteToClient: %s", msg));
                    out.println(msg);
                }
            } 
            catch (IOException ex) {
                Logger.getLogger(ClientCommunicationTask.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (SocketException ex) {
            Logger.getLogger(ClientCommunicationTask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientCommunicationTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JsonObject processInput(String input) {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(input.getBytes());
        JsonReader jsonReader = Json.createReader(byteInStream);
        JsonObject ob = jsonReader.readObject();
        PieLogger.info(this.getClass(), String.format("ConnectionText: %s", ob.toString()));
        return ob;
    }*/
}}
