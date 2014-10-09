package org.pieShare.pieTools.piePlate.service.cluster.api;

import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IPieEventTask;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public interface IClusterService {

	void connect(String clusterName) throws ClusterServiceException;
	
	void disconnect() throws ClusterServiceException;

	void sendMessage(IPieMessage msg) throws ClusterServiceException;

	int getMembersCount();

	boolean isConnectedToCluster();

	<P extends IPieMessage, T extends IPieEventTask<P>> void registerTask(Class<P> event, Class<T> task);
	
	String getName();
}
