package org.pieShare.pieTools.pieCeption.service.core.api;

import org.pieShare.pieTools.pieCeption.service.core.exception.PieCeptionServiceException;

/**
 * Created by Svetoslav on 29.12.13.
 */
public interface IPieCeptionConnectorService {
    /**
     * Connects to the already running pieInstance.
     */
    void connectToMaster() throws PieCeptionServiceException;

    //todo-sv: can service name be set over pom file of the pieInstance using this package?
    void connectToMaster(String serviceName) throws PieCeptionServiceException;

    boolean isPieShareRunning() throws PieCeptionServiceException;
}
