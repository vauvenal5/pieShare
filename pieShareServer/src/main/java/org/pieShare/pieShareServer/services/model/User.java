/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services.model;

import java.util.HashMap;

/**
 *
 * @author Richard
 */
public class User {

    private String idName;
    private HashMap<String, Client> clients;

    public User() {
        this.clients = new HashMap<String, Client>();
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String id) {
        this.idName = id;
    }

    public HashMap<String, Client> getClients() {
        return clients;
    }

    public void setClients(HashMap<String, Client> cleints) {
        this.clients = cleints;
    }
}
