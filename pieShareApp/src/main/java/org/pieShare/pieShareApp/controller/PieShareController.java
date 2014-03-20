package org.pieShare.pieShareApp.controller;

import org.pieShare.pieShareApp.model.SimpleMessage;
import org.pieShare.pieShareApp.model.task.PrintTask;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by vauvenal5 on 3/20/14.
 */
public class PieShareController {

    private IClusterService clusterService;

    public void setClusterService(IClusterService service) {
        this.clusterService = service;
    }

    public void run(){

        //init program
        PrintTask printTask = new PrintTask();

        this.clusterService.registerTask(SimpleMessage.class, printTask);

        try {
            this.clusterService.connect("testCloud");
        } catch (ClusterServiceException e) {
            e.printStackTrace();
        }

        while(!this.clusterService.isConnectedToCluster()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        SimpleMessage msg = new SimpleMessage();
        try {
            msg.setMsg(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            this.clusterService.sendMessage(msg);
        } catch (ClusterServiceException e) {
            e.printStackTrace();
        }
    }
}
