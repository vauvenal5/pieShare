package org.pieTools.pieCeption.service.core.api;

/**
 * Created by Svetoslav on 29.12.13.
 */
public interface IPieCeptionConnectorService {
    /**
     * Connects to the already running pieInstance.
     */
    void connectToMaster(String serviceName);
}
