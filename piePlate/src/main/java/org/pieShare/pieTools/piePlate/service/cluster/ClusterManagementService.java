/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service.cluster;

import java.util.Map;
import java.util.Map.Entry;
import org.pieShare.pieTools.piePlate.model.PiePlateBeanNames;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterAddedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterRemovedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterAddedListener;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterRemovedListener;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
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

	@Override
	public void sendMessage(IPieMessage message) throws ClusterManagmentServiceException {
		this.sendMessage(message, message.getAddress().getClusterName());
	}

	@Override
	public void disconnect(String id) throws ClusterServiceException {
		this.clusters.get(id).disconnect();
	}

	@Override
	public IClusterService connect(String id) throws ClusterManagmentServiceException {
		if (this.clusters.containsKey(id)) {
			return this.clusters.get(id);
		}

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
		}
		catch (BeanServiceError | ClusterServiceException ex) {
			//should never happen
			throw new ClusterManagmentServiceException(ex);
		}
	}

	@Override
	public void sendMessage(IPieMessage message, String cloudName) throws ClusterManagmentServiceException {
		if (!this.clusters.containsKey(cloudName)) {
			throw new ClusterManagmentServiceException(String.format("Cloud name not found: %s", cloudName));
		}

		try {
			this.clusters.get(cloudName).sendMessage(message);
		}
		catch (ClusterServiceException ex) {
			throw new ClusterManagmentServiceException(ex);
		}
	}

	@Override
	public void diconnectAll() throws ClusterManagmentServiceException {
		for (Entry<String, IClusterService> entry : this.clusters.entrySet()) {
			try {
				entry.getValue().disconnect();
			}
			catch (ClusterServiceException ex) {
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
