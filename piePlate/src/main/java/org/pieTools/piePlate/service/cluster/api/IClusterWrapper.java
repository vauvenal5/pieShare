package org.pieTools.piePlate.service.cluster.api;

import org.pieTools.piePlate.dto.PieMessage;
import org.pieTools.piePlate.service.exception.ClusterServiceException;

/**
 * Created by Svetoslav on 13.01.14.
 */
public interface IClusterWrapper {
    void sendMessage(PieMessage msg) throws ClusterServiceException;

    int getMembersCount();

    boolean isConnectedToCluster();
}
