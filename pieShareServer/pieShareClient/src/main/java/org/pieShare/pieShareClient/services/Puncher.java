/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareClient.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.pieShare.pieShareClient.api.Callback;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author RicLeo00
 */
public class Puncher implements Runnable {

    private BufferedReader reader;
    private Callback back;
    private PrintWriter out;

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public void setBack(Callback back) {
        this.back = back;
    }

    public void WaitForACKPunch() {

        while (true) {
            try {
                String fromServer = reader.readLine();
                JsonObject input = processInput(fromServer);

                if (input.getString("type").equals("ACK")) {
                    back.Handle();
                    return;
                }

                if (input.getString("type").equals("punch")) {
                    SendACK(out);
                }

            } catch (Exception ex) {
                PieLogger.info(this.getClass(), "Timeout...", ex);
            }
        }
    }

    public void SendACK(PrintWriter out) {
        String ackMsg = "{\"type\":\"ACK\"}";
        out.println(ackMsg);
    }

    public JsonObject processInput(String input) {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(input.getBytes());
        JsonReader jsonReader = Json.createReader(byteInStream);
        JsonObject ob = jsonReader.readObject();
        PieLogger.info(this.getClass(), String.format("ConnectionText: %s", ob.toString()));
        return ob;
    }

    public void run() {
        WaitForACKPunch();
    }
}
