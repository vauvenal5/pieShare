/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieTools.piePlate.model.PiePlateBeanNames;
import org.pieShare.pieTools.piePlate.model.message.api.IClusterMessage;
import org.pieShare.pieTools.piePlate.service.channel.api.IIncomingChannel;
import org.pieShare.pieTools.piePlate.service.channel.api.IOutgoingChannel;
import org.pieShare.pieTools.piePlate.service.channel.api.ITwoWayChannel;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterAddedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterRemovedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterAddedListener;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterRemovedListener;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.piePlate.service.loophole.LoopHoleFactory;
import org.pieShare.pieTools.piePlate.service.loophole.LoopHoleService;
import org.pieShare.pieTools.piePlate.service.loophole.api.ILoopHoleFactory;
import org.pieShare.pieTools.piePlate.service.loophole.api.ILoopHoleService;
import org.pieShare.pieTools.piePlate.service.loophole.event.NewLoopHoleConnectionEvent;
import org.pieShare.pieTools.piePlate.service.loophole.event.api.INewLoopHoleConnectionEventListener;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanServiceError;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Svetoslav
 */
public class ClusterManagementService implements IClusterManagementService {

    private Map<String, IClusterService> clusters;
    private IBeanService beanService;
    private IEventBase<IClusterAddedListener, ClusterAddedEvent> clusterAddedEventBase;
    private IEventBase<IClusterRemovedListener, ClusterRemovedEvent> clusterRemovedEventBase;
    private IClusterService returnService = null;
    private boolean sleep;

    @Override
    public IEventBase<IClusterAddedListener, ClusterAddedEvent> getClusterAddedEventBase() {
        return this.clusterAddedEventBase;
    }

    public void setClusterAddedEventBase(IEventBase<IClusterAddedListener, ClusterAddedEvent> clusterAddedEventBase) {
        this.clusterAddedEventBase = clusterAddedEventBase;
    }

    @Override
    public IEventBase<IClusterRemovedListener, ClusterRemovedEvent> getClusterRemovedEventBase() {
        return this.clusterRemovedEventBase;
    }

    public void setClusterRemovedEventBase(IEventBase<IClusterRemovedListener, ClusterRemovedEvent> clusterRemovedEventBase) {
        this.clusterRemovedEventBase = clusterRemovedEventBase;
    }

    public void setBeanService(IBeanService service) {
        this.beanService = service;
    }

    public void setMap(Map<String, IClusterService> map) {
        this.clusters = map;
    }

    private IClusterService connect(String id) throws ClusterManagmentServiceException {

        if (this.clusters.containsKey(id)) {
            return this.clusters.get(id);
        }

        ILoopHoleFactory loopHoleFactory = beanService.getBean(LoopHoleFactory.class);

        loopHoleFactory.getNewLoopHoleConnectionEvent().addEventListener(new INewLoopHoleConnectionEventListener() {
            @Override
            public void handleObject(NewLoopHoleConnectionEvent event) {
                try {
                    returnService = initConnection(id);
                    sleep = false;
                } catch (ClusterManagmentServiceException ex) {
                    PieLogger.error(this.getClass(), "Error", ex);
                }
            }
        });

        loopHoleFactory.setName(id);
        loopHoleFactory.initializeNewLoopHole();

        while (sleep) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClusterManagementService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return returnService;
    }

    private IClusterService initConnection(String id) throws ClusterManagmentServiceException {

        try {
            IClusterService cluster = (IClusterService) this.beanService.getBean(PiePlateBeanNames.getClusterService());
            cluster.setId(id);
            cluster.connect(id);
            this.clusters.put(id, cluster);
            this.clusterAddedEventBase.fireEvent(new ClusterAddedEvent(this, cluster));
            cluster.getClusterRemovedEventBase().addEventListener((IClusterRemovedListener) (ClusterRemovedEvent event) -> {
                clusters.remove(((IClusterService) event.getSource()).getId());
                clusterRemovedEventBase.fireEvent(event);
            });

            return cluster;
        } catch (BeanServiceError | ClusterServiceException ex) {
            //should never happen
            throw new ClusterManagmentServiceException(ex);
        }
    }

    @Override
    public void sendMessage(IClusterMessage message) throws ClusterManagmentServiceException {
        if (!this.clusters.containsKey(message.getAddress().getClusterName())) {
            throw new ClusterManagmentServiceException(String.format("Cloud name not found: %s", message.getAddress().getClusterName()));
        }

        try {
            this.clusters.get(message.getAddress().getClusterName()).sendMessage(message);
        } catch (ClusterServiceException ex) {
            throw new ClusterManagmentServiceException(ex);
        }
    }

    @Override
    public void registerChannel(String clusterId, IIncomingChannel channel) throws ClusterManagmentServiceException {
        IClusterService cluster = this.connect(clusterId);
        cluster.registerIncomingChannel(channel);
    }

    @Override
    public void registerChannel(String clusterId, IOutgoingChannel channel) throws ClusterManagmentServiceException {
        IClusterService cluster = this.connect(clusterId);
        cluster.registerOutgoingChannel(channel);
    }

    @Override
    public void registerChannel(String clusterId, ITwoWayChannel channel) throws ClusterManagmentServiceException {
        IClusterService cluster = this.connect(clusterId);
        cluster.registerIncomingChannel(channel);
        cluster.registerOutgoingChannel(channel);
    }

    @Override
    public void disconnect(String id) throws ClusterServiceException {
        this.clusters.get(id).disconnect();
    }

    @Override
    public void diconnectAll() throws ClusterManagmentServiceException {
        for (Entry<String, IClusterService> entry : this.clusters.entrySet()) {
            try {
                entry.getValue().disconnect();
            } catch (ClusterServiceException ex) {
                //todo: error handling
                PieLogger.error(this.getClass(), "Disconnect all failed!", ex);
            }
        }
    }

    @Override
    public Map<String, IClusterService> getClusters() {
        return this.clusters;
    }
}
