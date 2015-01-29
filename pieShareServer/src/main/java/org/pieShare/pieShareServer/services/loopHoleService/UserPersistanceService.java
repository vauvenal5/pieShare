/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services.loopHoleService;

import java.util.HashMap;
import java.util.function.BiConsumer;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.RegisterMessage;
import org.pieShare.pieShareServer.services.loopHoleService.api.IUserPersistanceService;
import org.pieShare.pieShareServer.services.model.Client;
import org.pieShare.pieShareServer.services.model.SubClient;
import org.pieShare.pieShareServer.services.model.User;

/**
 *
 * @author Richard
 */
public class UserPersistanceService implements IUserPersistanceService {

    private HashMap<String, User> newUsers;

    public UserPersistanceService() {
        this.newUsers = new HashMap<>();
    }

    
    @Override
    public synchronized void addUser(User msg) {
        newUsers.put(msg.getIdName(), msg);
    }
    
    @Override
    public synchronized User getByID(String name) {
        return newUsers.get(name);
    }

    @Override
    public HashMap<String, Client> getClients(String name) {
        return newUsers.get(name).getClients();
    }

    public SubClient getUnconnectedSubClientFromClient(Client client) {
        SubClient clientReturn = null;
        client.getSubClients().forEach((k, v) -> {
            if (v.getConnectedTo() != null) {
               // clientReturn = v;
            }
        });
        return clientReturn;
    }

    /* @Override
     public synchronized HashMap<String, User> getConnectedUsersByName(String name) {
     HashMap<String, User> returnUsers = new HashMap<>();
     newUsers.forEach((k, v) -> {
     if (v.getName().equals(name) && v.getConnectedTo() != null) {
     returnUsers.put(k, v);
     }
     });
     return returnUsers;
     }

     @Override
     public synchronized HashMap<String, User> getNonConnectedUsersByName(String name) {
     HashMap<String, User> returnUsers = new HashMap<>();
     newUsers.forEach((k, v) -> {
     if (v.getName().equals(name) && v.getConnectedTo() == null) {
     returnUsers.put(k, v);
     }
     });
     return returnUsers;
     }*/
    @Override
    public synchronized void deleteUser(String id) {
        newUsers.remove(id);
    }

    /* @Override
     public void mergeUser(User user) {
     User dbUser = newUsers.get(user.getLoopHoleID());
     dbUser.setConnectedTo(user.getConnectedTo());
     dbUser.setId(user.getId());
     dbUser.setName(user.getName());
     dbUser.setPrivateAddress(user.getPrivateAddress());
     dbUser.setPublicAddress(user.getPublicAddress());
     }*/
}
