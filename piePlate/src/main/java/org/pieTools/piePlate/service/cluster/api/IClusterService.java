package org.pieTools.piePlate.service.cluster.api;

import org.pieTools.piePlate.dto.PieMessage;
import org.pieTools.piePlate.service.exception.ClusterServiceException;

/**
 * Created by vauvenal5 on 12/12/13.
 */
public interface IClusterService {
    <P extends IPieMessage> void registerTask(Class<P> clazz, IMessageTask<P> task);

    void connect(String clusterName) throws ClusterServiceException;

    void sendMessage(PieMessage msg) throws ClusterServiceException;

    int getMembersCount();

    boolean isConnectedToCluster();
}
