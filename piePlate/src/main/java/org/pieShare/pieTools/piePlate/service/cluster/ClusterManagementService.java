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
import javafx.animation.KeyValue;
import org.pieShare.pieTools.piePlate.model.PiePlateBeanNames;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanService;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanServiceError;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;

/**
 *
 * @author Svetoslav
 */
public class ClusterManagementService implements IClusterManagementService {

	private Map<String, IClusterService> clusters;
	private IBeanService beanService;

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
	public IClusterService connect(String id) throws ClusterManagmentServiceException {
		if (this.clusters.containsKey(id)) {
			return this.clusters.get(id);
		}

		try {
			IClusterService cluster = (IClusterService) this.beanService.getBean(PiePlateBeanNames.getClusterService());
			cluster.connect(id);
			this.clusters.put(id, cluster);
			return cluster;
		} catch (BeanServiceError ex) {
			//should never happen
			throw new ClusterManagmentServiceException(ex);
		} catch (ClusterServiceException ex) {
			throw new ClusterManagmentServiceException(ex);
		}
	}

	@Override
	public void sendMessage(IPieMessage message, String cloudName) throws ClusterManagmentServiceException {
		if (this.clusters.containsKey(cloudName)) {
			try {
				this.clusters.get(cloudName).sendMessage(message);
			} catch (ClusterServiceException ex) {
				throw new ClusterManagmentServiceException(ex);
			}
		}
	}

	@Override
	public void diconnectAll() throws ClusterManagmentServiceException {
		for(Entry<String, IClusterService> entry : this.clusters.entrySet()) {
			try {
				entry.getValue().disconnect();
			} catch (ClusterServiceException ex) {
				//todo: error handling
				Logger.getLogger(ClusterManagementService.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	@Override
	public Map<String, IClusterService> getClusters() {
		return this.clusters;
	}
}
