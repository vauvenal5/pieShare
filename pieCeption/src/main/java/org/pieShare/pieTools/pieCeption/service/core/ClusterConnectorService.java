package org.pieShare.pieTools.pieCeption.service.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.Validate;
import org.pieShare.pieTools.pieCeption.service.action.CommandAction;
import org.pieShare.pieTools.pieCeption.service.action.ICommand;
import org.pieShare.pieTools.pieCeption.service.core.api.IConnectorService;
import org.pieShare.pieTools.pieCeption.service.core.exception.PieCeptionServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;

/**
 * Created by Svetoslav on 30.12.13.
 */
public class ClusterConnectorService implements IConnectorService {

    private IClusterService clusterService;
    private IClusterManagementService clusterManagementService;
    
    @PostConstruct
    public void init() {
        try {
            String serviceName = InetAddress.getLocalHost().getHostName();
            this.clusterService = this.clusterManagementService.connect(serviceName);
        } catch (UnknownHostException ex) {
            //todo-sv: error handling
        } catch (ClusterManagmentServiceException ex) {
            //todo-sv: error handling
        }
    }
    
    public void setClusterManagementService(IClusterManagementService service) {
        this.clusterManagementService = service;
    }

    @Override
    public boolean isPieShareRunning() {
        if(this.clusterService == null) {
            this.init();
        }
        
        if(this.clusterService.getMembersCount() > 1){
            return true;
        }
        return false;
    }

    @Override
    public void sendToMaster(ICommand command) {
        try {
            this.clusterService.sendMessage(command.getMessage());
        } catch (ClusterServiceException ex) {
            //todo-sv error handling
        }
    }
}
