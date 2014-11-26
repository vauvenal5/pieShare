package org.pieShare.pieTools.piePlate.service.cluster.api;

import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.service.cluster.event.ClusterRemovedEvent;
import org.pieShare.pieTools.piePlate.service.cluster.event.IClusterRemovedListener;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.service.eventBase.IEventBase;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public interface IClusterService {

	void connect(String clusterName) throws ClusterServiceException;
	
	String getId();
	
	void setId(String id);
	
	void disconnect() throws ClusterServiceException;

	void sendMessage(IPieMessage msg, EncryptedPassword encPwd) throws ClusterServiceException;

	int getMembersCount();

	boolean isConnectedToCluster();
	
	IEventBase<IClusterRemovedListener, ClusterRemovedEvent> getClusterRemovedEventBase();
}
