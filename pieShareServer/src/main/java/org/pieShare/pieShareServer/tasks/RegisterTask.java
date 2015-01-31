/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.tasks;

import java.net.InetSocketAddress;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleConnectionMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.RegisterMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import org.pieShare.pieShareServer.services.loopHoleService.api.ILoopHoleService;
import org.pieShare.pieShareServer.services.loopHoleService.api.IUserPersistanceService;
import org.pieShare.pieShareServer.services.model.Client;
import org.pieShare.pieShareServer.services.model.SubClient;
import org.pieShare.pieShareServer.services.model.User;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.FirstLoopHoleUserMessage;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class RegisterTask implements IPieEventTask<RegisterMessage> {

    private RegisterMessage msg;
    private IUserPersistanceService userPersistanceService;
    private ILoopHoleService loopHoleService;

    public void setLoopHoleService(ILoopHoleService loopHoleService) {
        this.loopHoleService = loopHoleService;
    }

    public void setUserPersistanceService(IUserPersistanceService userPersistanceService) {
        this.userPersistanceService = userPersistanceService;
    }

    @Override
    public void setEvent(RegisterMessage msg) {
        this.msg = msg;
    }

    @Override
    public void run() {

        InetSocketAddress senderAddress = new InetSocketAddress(msg.getSenderHost(), msg.getSenderPort());
        
        User newUser = userPersistanceService.getByID(msg.getName());

        if (newUser == null) {
            newUser = new User();
            newUser.setIdName(msg.getName());
            userPersistanceService.addUser(newUser);
        }

        if (newUser.getClients().isEmpty()) {
            PieLogger.debug(this.getClass(), "FIRST CLIENT..Sending message!");
            FirstLoopHoleUserMessage loopHoleUserMessage = new FirstLoopHoleUserMessage();
            loopHoleUserMessage.setLocalLoopID(msg.getLocalLoopID());
            msg.setSenderID(msg.getSenderID());
            loopHoleService.send(loopHoleUserMessage, senderAddress);
        } else {
            PieLogger.debug(this.getClass(), "NOT FIRST CLIENT!");
        }

        Client client = newUser.getClients().get(msg.getSenderID());
        if (client == null) {
            client = new Client();
            client.setId(msg.getSenderID());
            newUser.getClients().put(msg.getSenderID(), client);
        }

        SubClient subClient = client.getSubClients().get(msg.getLocalLoopID());
        if (subClient == null) {
            subClient = new SubClient();
            client.getSubClients().put(msg.getLocalLoopID(), subClient);
        }

        InetSocketAddress privateAddress = new InetSocketAddress(msg.getPrivateHost(), msg.getPrivatePort());

        subClient.setPrivateAddress(privateAddress);
        subClient.setPublicAddress(senderAddress);
        subClient.setLoopHoleID(msg.getLocalLoopID());

        client.getSubClients().put(subClient.getLoopHoleID(), subClient);

        PieLogger.info(this.getClass(), String.format("User:        %s  with SubUD: %s   Registered Sucessfully.", newUser.getIdName(), msg.getLocalLoopID()));
        PieLogger.info(this.getClass(), String.format("PublicHost:  %s, PublicPort:  %s", subClient.getPublicAddress().getAddress().getHostAddress(), subClient.getPublicAddress().getPort()));
        PieLogger.info(this.getClass(), String.format("PrivateHost: %s, PrivatePort: %s", subClient.getPrivateAddress().getAddress().getHostAddress(), subClient.getPrivateAddress().getPort()));

        LoopHoleConnectionMessage connectionMessageToReceiver = new LoopHoleConnectionMessage();
        connectionMessageToReceiver.setClientPrivateIP(subClient.getPrivateAddress().getAddress().getHostAddress());
        connectionMessageToReceiver.setClientPrivatePort(subClient.getPrivateAddress().getPort());
        connectionMessageToReceiver.setClientPublicIP(subClient.getPublicAddress().getAddress().getHostAddress());
        connectionMessageToReceiver.setClientPublicPort(subClient.getPublicAddress().getPort());
        connectionMessageToReceiver.setFromId(client.getId());

        for (Client cl : newUser.getClients().values()) {
            if (cl.getId().equals(msg.getSenderID())) {
                continue;
            }

            SubClient subToUse = null;
            for (SubClient sub : cl.getSubClients().values()) {
                if (sub.getConnectedTo() == null) {
                    subToUse = sub;
                }

                if (sub.getConnectedTo() != null && sub.getConnectedTo().equals(msg.getSenderID())) {
                    subToUse = null;
                    break;
                }
            }

            if (subToUse != null) {
                LoopHoleConnectionMessage connectionMessageToSender = new LoopHoleConnectionMessage();
                connectionMessageToSender.setClientPrivateIP(subToUse.getPrivateAddress().getAddress().getHostAddress());
                connectionMessageToSender.setClientPrivatePort(subToUse.getPrivateAddress().getPort());
                connectionMessageToSender.setClientPublicIP(subToUse.getPublicAddress().getAddress().getHostAddress());
                connectionMessageToSender.setClientPublicPort(subToUse.getPublicAddress().getPort());
                connectionMessageToSender.setFromId(cl.getId());
                connectionMessageToSender.setSenderID(msg.getSenderID());
                connectionMessageToSender.setLocalLoopID(msg.getLocalLoopID());
                connectionMessageToSender.setClientLocalLoopID(subToUse.getLoopHoleID());

                loopHoleService.send(connectionMessageToSender, senderAddress);

                connectionMessageToReceiver.setSenderID(cl.getId());
                connectionMessageToReceiver.setLocalLoopID(subToUse.getLoopHoleID());
                connectionMessageToReceiver.setClientLocalLoopID(msg.getLocalLoopID());

                loopHoleService.send(connectionMessageToReceiver, subToUse.getPublicAddress());

                subClient.setConnectedTo(cl.getId());
                //userPersistanceService.mergeUser(user);
                subToUse.setConnectedTo(client.getId());
                break;
            }

        }

        /* HashMap<String, User> notConnectedUsers = userPersistanceService.getNonConnectedUsersByName(msg.getName());
         for (User user : notConnectedUsers.values()) {
         PieLogger.info(this.getClass(), String.format("Found non connected users. Count: %s", notConnectedUsers.size()));

         if (!msg.getSenderID().equals(user.getId())) {

         LoopHoleConnectionMessage connectionMessageToSender = new LoopHoleConnectionMessage();
         connectionMessageToSender.setClientPrivateIP(subClient.getPrivateAddress().getHost());
         connectionMessageToSender.setClientPrivatePort(subClient.getPrivateAddress().getPort());
         connectionMessageToSender.setClientPublicIP(subClient.getPublicAddress().getHost());
         connectionMessageToSender.setClientPublicPort(subClient.getPublicAddress().getPort());
         connectionMessageToSender.setFromId(user.getId());
         connectionMessageToSender.setSenderID(msg.getSenderID());
         connectionMessageToSender.setLocalLoopID(msg.getLocalLoopID());
         connectionMessageToSender.setClientLocalLoopID(subClient.getLoopHoleID());

         loopHoleService.send(connectionMessageToSender, msg.getSenderAddress());

         connectionMessageToReceiver.setSenderID(user.getId());
         connectionMessageToReceiver.setLocalLoopID(subClient.getLoopHoleID());
         connectionMessageToReceiver.setClientLocalLoopID(msg.getLocalLoopID());

         loopHoleService.send(connectionMessageToReceiver, subClient.getPublicAddress());

         subClient.setConnectedTo(msg.getLocalLoopID());
         userPersistanceService.mergeUser(user);
         subClient.setConnectedTo(subClient.getLoopHoleID());
         break;
         }
         }

         userPersistanceService.addUser(newUser);

         /*
         for (User user : notConnectedUsers.values()) {

         if (!msg.getSenderID().equals(user.getId())) {

         LoopHoleConnectionMessage connectionMessageToSender = new LoopHoleConnectionMessage();
         connectionMessageToSender.setClientPrivateIP(user.getPrivateAddress().getHost());
         connectionMessageToSender.setClientPrivatePort(user.getPrivateAddress().getPort());
         connectionMessageToSender.setClientPublicIP(user.getPublicAddress().getHost());
         connectionMessageToSender.setClientPublicPort(user.getPublicAddress().getPort());
         connectionMessageToSender.setFromId(user.getId());
         connectionMessageToSender.setSenderID(msg.getSenderID());
         loopHoleService.send(connectionMessageToSender, msg.getSenderAddress());
         }
         connectionMessageToReceiver.setSenderID(user.getId());
         loopHoleService.send(connectionMessageToReceiver, user.getPublicAddress());
         }*/
    }
}
