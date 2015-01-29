/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services.model;

import java.util.HashMap;

/**
 *
 * @author RicLeo00
 */
public class Client {

    private String name;
    private String clientID;
    private HashMap<String, SubClient> subClients;

    public Client() {
        this.subClients = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public HashMap<String, SubClient> getSubClients() {
        return subClients;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return clientID;
    }

    public void setId(String id) {
        this.clientID = id;
    }
}
